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
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.OAuth2Definition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import java.lang.reflect.Method

class SwaggerService {

    GrailsApplication grailsApplication
    GrailsUrlService grailsUrlService

    private Map<Api, List<ApiOperation>> _apiOperations

    Collection<Tag> getTags() {
        List<Tag> tags = []
        apis.each { Api api ->
            List<String> tagNames = api.tags().grep() ?: [api.value()]
            tagNames.each { String tag ->
                tags << new Tag(name: tag, description: api.description())
            }
        }
        return tags
    }

    Map<String, SecuritySchemeDefinition> getSecurityDefinitions() {
        getSecurityDefinitions(apis)
    }
    Map<String, SecuritySchemeDefinition> getSecurityDefinitions(Collection<Api> apis) {
        Map<String, SecuritySchemeDefinition> secDefinitions = [:]
        apis.each { Api api ->
            api.authorizations().findAll{ it.value() != "" }.each { Authorization auth ->
                SecuritySchemeDefinition definition
                switch(auth.type()) {
                    case "apiKey" :
                        definition = new ApiKeyAuthDefinition()
                        break
                    case "basic" :
                        definition = new BasicAuthDefinition()
                        break
                    case "oauth2" :
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
        return secDefinitions
    }

    Map<String, Path> getPaths() {
        Map<String, Path> apiPaths = [:]
        grailsApplication.controllerClasses.each { GrailsControllerClass grailsController ->
            Api api = grailsController.clazz.getAnnotation(Api)
            if(api) {
                grailsController.clazz.methods.findAll { it.getAnnotation(ApiOperation) || it.getAnnotation(ApiResponses) }.each {Method action ->
                    String pathStr = grailsUrlService.getPathForAction(grailsController, action)
                    if(!apiPaths.containsKey(pathStr)) {
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
        apiPaths
    }

    private synchronized Map<Api, List<ApiOperation>> getApiOperations() {
        if(!_apiOperations) {
            _apiOperations = grailsApplication.controllerClasses.collectEntries { GrailsControllerClass controllerClass ->
                Api api = controllerClass.clazz.getAnnotation(Api)
                if(api) {
                    List<ApiOperation> operations = controllerClass.clazz.methods.collect { Method action ->
                        action.getAnnotation(ApiOperation)
                    }.grep()
                    return [(api) : (operations)]
                }
                return [:]
            }
        }
        _apiOperations
    }

    private Set<Api> getApis() {
        return apiOperations.keySet()
    }
}
