package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.OAuth2Definition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass

class SwaggerService {

    GrailsApplication grailsApplication

    private List<Api> _apis

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
    Map<String, SecuritySchemeDefinition> getSecurityDefinitions(List<Api> apis) {
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

    private List<Api> getApis() {
        if(!_apis) {
            _apis = grailsApplication.getControllerClasses().collect { GrailsControllerClass it ->
                it.clazz.getAnnotation(Api)
            }.grep()
        }
        return _apis
    }
}
