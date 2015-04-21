package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Operation
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Response
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.OAuth2Definition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import java.lang.reflect.Method

class SwaggerService {

    GrailsApplication grailsApplication
    GrailsUrlService grailsUrlService

    private Map<Api, List<ApiOperation>> _apiOperations

    Collection<Tag> getTags(Swagger swagger) {
        List<Tag> apiTags = swagger.tags ?: []
        getApplicationApis().each { Api api ->
            List<String> tagNames = api.tags().grep() ?: [api.value()]
            tagNames.each { String tag ->
                apiTags << new Tag(name: tag, description: api.description())
            }
        }
        swagger.tags = apiTags
    }

    Map<String, SecuritySchemeDefinition> getSecurityDefinitions(Swagger swagger, Collection<Api> apis = getApplicationApis()) {
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

    Map<String, Path> getPaths(Swagger swagger, List<GrailsClass> controllers = grailsApplication.controllerClasses as List) {

        Map<String, Path> apiPaths = swagger.paths ?: [:]

        controllers.each { GrailsControllerClass grailsController ->
            Api api = grailsController.clazz.getAnnotation(Api)
            if (api) {
                grailsController.clazz.methods.findAll {
                    it.getAnnotation(ApiOperation) || it.getAnnotation(ApiResponses)
                }.each { Method action ->
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
                    path.get(operation)
                }
            }
        }
        swagger.paths = apiPaths
    }

    synchronized Map<Api, List<ApiOperation>> getApplicationApiOperations(List<GrailsClass> controllers = grailsApplication.controllerClasses as List) {
        controllers.collectEntries { GrailsControllerClass controllerClass ->
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

    Set<Api> getApplicationApis(List<GrailsClass> controllers = grailsApplication.controllerClasses as List) {
        return getApplicationApiOperations().keySet()
    }
}