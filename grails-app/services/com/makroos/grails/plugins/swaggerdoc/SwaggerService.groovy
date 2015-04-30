package com.makroos.grails.plugins.swaggerdoc

import com.makroos.grails.plugins.swaggerdoc.property.PropertyHelper
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.ModelImpl
import com.wordnik.swagger.models.Operation
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Response
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.OAuth2Definition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import com.wordnik.swagger.models.properties.ArrayProperty
import com.wordnik.swagger.models.properties.Property
import com.wordnik.swagger.models.properties.RefProperty
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.springframework.http.HttpStatus

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class SwaggerService {

    GrailsApplication grailsApplication
    GrailsUrlService grailsUrlService

    List<Tag> getTags(Swagger swagger, Collection<GrailsClass> controllers = applicationControllers) {
        List<Tag> apiTags = swagger.tags ?: []
        getApplicationApis(controllers).each { Api api ->
            apiTags += getTagsForApi(api)
        }
        swagger.tags = apiTags
    }

    private List<Tag> getTagsForApi(Api api) {
        List<String> tagNames = api.tags().grep() ?: [api.value()]
        tagNames.collect { String tag ->
            new Tag(name: tag, description: api.description())
        }
    }

    Map<String, SecuritySchemeDefinition> getSecurityDefinitions(Swagger swagger, Collection<GrailsClass> controllers = applicationControllers) {
        Collection<Api> apis = getApplicationApis(controllers)
        Map<String, SecuritySchemeDefinition> secDefinitions = swagger.securityDefinitions ?: [:]
        apis.each { Api api ->
            api.authorizations().findAll { it.value() != "" }.each { Authorization auth ->
                SecuritySchemeDefinition definition
                switch (auth.type()) {
                    case "apiKey":
                        definition = new ApiKeyAuthDefinition()
                        break
                    case "basic":
                        definition = new BasicAuthDefinition()
                        break
                    case "oauth2":
                        definition = new OAuth2Definition()
                        definition.scopes =
                                auth.scopes().collectEntries { AuthorizationScope scope ->
                                    [(scope.scope()): scope.description()]
                                }
                        break

                    default: definition = new SecuritySchemeDefinition() {
                        private String type = "other"

                        String getType() {
                            return this.type
                        }

                        void setType(String type) {
                            this.type = type
                        }
                    }
                }
                definition.type = auth.type()
                secDefinitions[auth.value()] = definition
            }
        }
        swagger.securityDefinitions = secDefinitions
    }

    Map<String, Path> getPaths(Swagger swagger, List<GrailsClass> controllers = applicationControllers) {

        Map<String, Path> apiPaths = swagger.paths ?: [:]

        controllers.each { GrailsControllerClass grailsController ->
            Api api = grailsController.clazz.getAnnotation(Api)
            if (api) {
                grailsController.clazz.methods.findAll {
                    it.getAnnotation(ApiOperation) || it.getAnnotation(ApiResponses)
                }.each { Method action ->
                    log.debug("Adding path for ${grailsController.shortName}.${action.name}()")
                    String pathStr = grailsUrlService.getPathForAction(grailsController, action)
                    pathStr -= swagger.basePath
                    if (!apiPaths.containsKey(pathStr)) {
                        apiPaths[pathStr] = new Path()
                    }
                    Path path = apiPaths[pathStr]

                    //TODO: Support multiple HTTP Methods
                    Operation operation = new Operation()
                    ApiResponses apiResponses = action.getAnnotation(ApiResponses)
                    apiResponses?.value().each { ApiResponse apiResponse ->
                        operation.response(apiResponse.code(), new Response(description: apiResponse.message()))
                    }
                    operation.tags(getTagsForApi(api)*.name)

                    //Build the Model definitions from the class specified on response
                    if(action.isAnnotationPresent(ApiOperation)) {
                        ApiOperation apiOperation = action.getAnnotation(ApiOperation)
                        Class responseClass = apiOperation.response()
                        if(responseClass != Void) {

                            //Add the definitions
                            fetchDefinitionsFrom(responseClass).each {
                                swagger.addDefinition(it.key, it.value)
                            }

                            //Include a reference in the operation response
                            Response response = operation.responses?.get('200') ?: new Response(description: HttpStatus.OK.reasonPhrase)
                            String ref = responseClass.getAnnotation(ApiModel)?.value() ?: responseClass.simpleName
                            Property responseProperty = new RefProperty(ref)
                            if(apiOperation.responseContainer()) {
                                assert apiOperation.responseContainer() == 'array', "The only supported responseContainer in @ApiOpration is 'array'"
                                Property arrayProperty = new ArrayProperty(responseProperty)
                                responseProperty = arrayProperty
                            }
                            response.schema(responseProperty)
                            operation.response(200,response)
                        }
                    }

                    path.get(operation)
                }
            }
        }
        swagger.paths = apiPaths
    }

    Map<String, Model> fetchDefinitionsFrom(Class clazz) {

        log.debug("Adding definition for ${clazz.simpleName}")

        Map<String, Model> models = [:]
        ModelImpl model = new ModelImpl()

        ApiModel apiModel = clazz.getAnnotation(ApiModel)
        model.name = apiModel?.value() ?: clazz.simpleName
        model.description = apiModel?.description() ?: null

        //Get all the fields which could be a property
        List<Field> propertyCandidates = clazz.declaredFields.findAll { Field field ->
            !Modifier.isStatic(field.modifiers) && !Modifier.isTransient(field.modifiers)
        } as List

        //Iterate and build the property objects
        propertyCandidates.each { Field field ->
            ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty)
            if(!modelProperty || !modelProperty.hidden()) {
                Property property = PropertyHelper.buildPropertyFor(field)
                if(property.type  == 'ref') {
                    models << fetchDefinitionsFrom(field.type)
                }
                model.addProperty(property.title, property)
            }
        }
        models << [(model.name):model]

        models
    }

    List<GrailsClass> getApplicationControllers() {
        grailsApplication.controllerClasses as List
    }

    synchronized Map<Api, List<ApiOperation>> getApplicationApiOperations(Collection<GrailsClass> controllers) {
        controllers.collectEntries { GrailsClass controllerClass ->
            Api api = controllerClass.clazz.getAnnotation(Api)
            if (api) {
                List<ApiOperation> operations = controllerClass.clazz.methods.collect { Method action ->
                    action.getAnnotation(ApiOperation)
                }.grep()
                return [(api): (operations)]
            }
            return [:]
        }
    }

    Set<Api> getApplicationApis(Collection<GrailsClass> controllers) {
        return getApplicationApiOperations(controllers).keySet()
    }
}