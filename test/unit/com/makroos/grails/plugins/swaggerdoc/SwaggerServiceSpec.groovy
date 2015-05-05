package com.makroos.grails.plugins.swaggerdoc

import com.makroos.grails.plugins.swaggerdoc.test.Code
import com.makroos.grails.plugins.swaggerdoc.test.Dog
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.annotations.Authorization
import com.wordnik.swagger.annotations.AuthorizationScope
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.BasicAuthDefinition
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import com.wordnik.swagger.models.parameters.Parameter
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import spock.lang.Ignore
import spock.lang.Shared

/**
 * Created by @marcos-carceles on 21/04/15.
 */
@TestFor(SwaggerService)
class SwaggerServiceSpec extends SwaggerSpecification {

    @Shared GrailsControllerClass sampleController

    def setupSpec() {
        sampleController = new DefaultGrailsControllerClass(SampleController)
    }

    def setup() {
        service.grailsUrlService = Mock(GrailsUrlService)
        _ * service.grailsUrlService.getPathForAction(petController, {it.name == 'show'}) >> "/api/pet/{id}"
        _ * service.grailsUrlService.getPathForAction(petController, {it.name == 'buy'}) >> "/api/pet/buy/{id}"
        _ * service.grailsUrlService.getPathForAction(petController, {it.name == 'index'}) >> "/api/pets"
        _ * service.grailsUrlService.getPathForAction(petController, {it.name == 'dogs'}) >> "/api/pet/dogs"
        _ * service.grailsUrlService.getPathForAction(petController, {it.name == 'save'}) >> "/api/pet/save"

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

    @Ignore
    void "can build the scopes out of the @Api"() {
    }

    void "can render different types of Authorization mechanisms"() {
        when:
        def securityDefinitions = service.getSecurityDefinitions(new Swagger(), [sampleController])

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
        1 * service.grailsUrlService.getPathForAction(sampleController, _) >> "/api/v1.0/unitTest/index"

        when:
        service.getPaths(swagger, [sampleController])

        then:
        swagger.paths.keySet() == [expected] as Set

        where:
        basePath    || expected
        null        || "/api/v1.0/unitTest/index"
        "/api/v1.0" || "/unitTest/index"
    }

    def "Swagger paths relate to Tags"() {
        given:
        Swagger swagger = new Swagger()
        1 * service.grailsUrlService.getPathForAction(tagsController, _) >> "/withTags/list"
        1 * service.grailsUrlService.getPathForAction(sampleController, _) >> "/sample/index"

        when:
        service.getPaths(swagger, [tagsController, sampleController])

        then:
        swagger.paths['/sample/index'].get.tags == ['sample']
        swagger.paths['/withTags/list'].get.tags == ["with_tag", "with_another_tag"]
    }

    def "Swagger paths describe their parameters"() {
        given:
        Swagger swagger = new Swagger(basePath: '/api')

        when:
        service.getPaths(swagger, [petController])
        Parameter showParameter = swagger.paths['/pet/{id}'].get.parameters[0]
        Parameter buyParameter = swagger.paths['/pet/buy/{id}'].get.parameters[0]

        then:
        showParameter.name == "id"
        showParameter.in == "path"
        showParameter.required == true
        showParameter.type == "integer"
        showParameter.format == "int64"

        and:
        buyParameter.name == "id"
        buyParameter.in == "path"
        buyParameter.required == true
        buyParameter.type == "integer"
        buyParameter.format == "int32"
    }

    def "Builds definitions based on Api Responses"() {
        given:
        Swagger swagger = new Swagger()

        when:
        service.getPaths(swagger, [petController])

        then:
        swagger.definitions.keySet() == ['Pet', 'Dog', 'PetBreed', 'APIResponse'] as Set

        and:
        swagger.definitions.Pet.required == ['collarNumber']
        swagger.definitions.Pet.properties.allergies.items.type == 'string'
        !swagger.definitions.Pet.properties.bittenSomeone
        swagger.definitions.Pet.properties.breed.$ref == '#/definitions/PetBreed'

        and:
        swagger.definitions.PetBreed.description == "Type of Pet"
        swagger.definitions.PetBreed.properties.subspecies.type == 'array'
        swagger.definitions.PetBreed.properties.subspecies.uniqueItems == true

        and:
        swagger.definitions.APIResponse.properties.code.type == 'string'
        swagger.definitions.APIResponse.properties.code.enum == Code.values()*.toString()
        swagger.definitions.APIResponse.properties.meta.type == 'object'
    }

    void "Includes all properties in Model (including inherited ones)"() {
        when:
        Map<String,Model> properties = service.fetchDefinitionsFrom(Dog)

        then: 'includes the declared fields'
        properties['Dog'].properties.keySet().contains('favouriteToy')

        and: 'includes inherited fields'
        properties['Dog'].properties.keySet().containsAll(['name','collarNumber','breed','dateOfBirth','allergies'])
    }
}

@Api(
        value="sample",
        authorizations = [
        @Authorization(value="testoauth2", type = "oauth2", scopes = [
                @AuthorizationScope(scope="resource:read", description = "Read priviledges"),
                @AuthorizationScope(scope="resource:write", description = "Write priviledges")
        ]),
        @Authorization(value="testapikey", type = "apiKey"),
        @Authorization(value="testother", type = "bespoke")
])
class SampleController {
    @ApiResponses(value=[ @ApiResponse(code = 500, message = "Ups, we messed it up") ])
    def index(){}
}
