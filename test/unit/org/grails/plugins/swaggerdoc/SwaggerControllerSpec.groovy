package org.grails.plugins.swaggerdoc

import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.util.Json
import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.util.Metadata

/**
 * Created by @marcos-carceles on 16/04/15.
 */
@TestFor(SwaggerController)
@Mock([SwaggerService, GrailsUrlService])
class SwaggerControllerSpec  extends SwaggerSpecification {

    def setupSpec() {
        config.swaggerdoc.swagger = [
            info : [
                version : "1.0.0",
                title : grails.util.Metadata.current['app.name']
            ],
            basePath : "/api",
            paths : [:]
        ]
        JSON.registerObjectMarshaller(Swagger, { Swagger swagger ->
            JSON.parse(Json.mapper().writeValueAsString(swagger))
        })
    }

    def setup() {
        controller.swaggerService.metaClass.getApplicationControllers = { appControllers }
    }

    void "index renders swagger info from configuration"() {
        when:
        params.format='json'
        controller.swagger()

        then:
        def json = controller.response.json
        json.info.title
        json.info.title == Metadata.current['app.name']
    }

    void "index renders the tags existing in the application"() {
        when:
        controller.swagger()

        then:
        def json = controller.response.json
        json.tags.containsAll([
                [name: 'pet', description: 'Pet operations'],
                [name: 'with_tag', description: 'When Api declares tags, it overrrides the description'],
                [name: 'with_another_tag', description: 'When Api declares tags, it overrrides the description']
        ])
        json.tags.size() == 3
    }

    void "index renders the security definitions"() {
        when:
        controller.swagger()
        then:
        def json = controller.response.json
        json.securityDefinitions['petauth'] == [type: 'basic']
    }
}
