package com.makroos.grails.plugins.swaggerdoc

import com.makroos.grails.plugins.swaggerdoc.test.PetController
import com.makroos.grails.plugins.swaggerdoc.test.WithTagsController
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 21/04/15.
 */
@TestFor(SwaggerService)
class SwaggerServiceSpec extends Specification {

    @Shared List<GrailsControllerClass> appControllers
    @Shared GrailsControllerClass testController

    def setupSpec() {
        appControllers = []
        appControllers << new DefaultGrailsControllerClass(PetController)
        appControllers << new DefaultGrailsControllerClass(WithTagsController)
        testController = new DefaultGrailsControllerClass(TestController)
    }

    void "can fetch tags"() {
        when:
        List<Tag> tags = service.getTags(new Swagger(), appControllers)

        then:
        tags.size() == 3
        def petTag = tags.find {
            it.name == 'pet'
        }
        petTag.description == 'Pet operations'
    }

    void "tags overrides value on @Api"() {
        when:
        List<Tag> withTags = service.getTags(new Swagger(), appControllers).findAll { it.description == 'When Api declares tags, it overrrides the description' }

        then:
        withTags.size() == 2
        withTags*.name == ['with_tag', 'with_another_tag']
    }

    void "can fetch securityDefinitions"() {
        when:
        Map<String, SecuritySchemeDefinition> securityDefinitions = service.getSecurityDefinitions(new Swagger(), appControllers)

        then:
        securityDefinitions['petauth'] instanceof BasicAuthDefinition
        securityDefinitions['petauth'].type == 'basic'
    }

    void "can build the scopes out of the @Api"() {
    }

    void "can render different types of Authorization mechanisms"() {
        when:
        def securityDefinitions = service.getSecurityDefinitions(new Swagger(), [testController])

        then: "Can build Oauth2"
        securityDefinitions['testoauth2'].scopes.size() == 2
        securityDefinitions['testoauth2'].scopes['resource:read'] == 'Read priviledges'
        securityDefinitions['testoauth2'].scopes['resource:write'] == 'Write priviledges'

        and: "Can build basic authentication"
        securityDefinitions['testapikey'].type == 'apiKey'
        and: "Can build custom authentication"
        securityDefinitions['testother'].type == 'bespoke'
    }

    def "Swagger paths are relative to the basePath"() {
        given:
        Swagger swagger = new Swagger(basePath: basePath)
        service.grailsUrlService = Mock(GrailsUrlService)
        1 * service.grailsUrlService.getPathForAction(testController, _) >> "/api/v1.0/unitTest/index"

        when:
        service.getPaths(swagger, [testController])

        then:
        swagger.paths.keySet() == [expected] as Set

        where:
        basePath    || expected
        null        || "/api/v1.0/unitTest/index"
        "/api/v1.0" || "/unitTest/index"
    }

    @Ignore
    void "determines the HTTP Methods"(){
        expect:
        false
    }
}

@Api(
        value="unitTest",
        authorizations = [
        @Authorization(value="testoauth2", type = "oauth2", scopes = [
                @AuthorizationScope(scope="resource:read", description = "Read priviledges"),
                @AuthorizationScope(scope="resource:write", description = "Write priviledges")
        ]),
        @Authorization(value="testapikey", type = "apiKey"),
        @Authorization(value="testother", type = "bespoke")
])
class TestController {

    @ApiResponses(value=[ @ApiResponse(code = 500, message = "Ups, we messed it up") ])
    def index(){}
}