package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class SwaggerServiceIntegrationSpec extends Specification {

    SwaggerService swaggerService

    def setup() {
    }

    def cleanup() {
    }

    void "can fetch tags"() {
        when:
        List<Tag> tags = swaggerService.getTags(new Swagger())

        then:
        tags.size() == 3
        def petTag = tags.find {
            it.name == 'pet'
        }
        petTag.description == 'Pet operations'
    }

    void "tags overrides value on @Api"() {
        when:
        List<Tag> withTags = swaggerService.getTags(new Swagger()).findAll { it.description == 'When Api declares tags, it overrrides the description' }

        then:
        withTags.size() == 2
        withTags*.name == ['with_tag', 'with_another_tag']
    }

    void "can fetch securityDefinitions"() {
        when:
        Map<String, SecuritySchemeDefinition> securityDefinitions = swaggerService.getSecurityDefinitions(new Swagger())

        then:
        securityDefinitions['petauth'] instanceof BasicAuthDefinition
        securityDefinitions['petauth'].type == 'basic'
    }

    void "can build the scopes out of the @Api"() {
    }

    void "can render different types of Authorization mechanisms"() {
        given:
        Api api = MultipleAuthApi.getAnnotation(Api)

        when:
        def securityDefinitions = swaggerService.getSecurityDefinitions(new Swagger(), [api])

        then: "Can build Oauth2"
        securityDefinitions['testoauth2'].scopes.size() == 2
        securityDefinitions['testoauth2'].scopes['resource:read'] == 'Read priviledges'
        securityDefinitions['testoauth2'].scopes['resource:write'] == 'Write priviledges'

        and: "Can build basic authentication"
        securityDefinitions['testapikey'].type == 'apiKey'
        and: "Can build custom authentication"
        securityDefinitions['testother'].type == 'bespoke'
    }

    @Ignore
    void "swaggerService determines the HTTP Methods"(){
        expect:
        false
    }
}

@Api(authorizations = [
        @Authorization(value="testoauth2", type = "oauth2", scopes = [
                @AuthorizationScope(scope="resource:read", description = "Read priviledges"),
                @AuthorizationScope(scope="resource:write", description = "Write priviledges")
        ]),
        @Authorization(value="testapikey", type = "apiKey"),
        @Authorization(value="testother", type = "bespoke")
])
class MultipleAuthApi {}
