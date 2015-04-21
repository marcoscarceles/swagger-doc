package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.models.Swagger
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 21/04/15.
 */
@TestFor(SwaggerService)
class SwaggerServiceSpec extends Specification {

    def "Swagger paths are relative to the basePath"() {
        given:
        Swagger swagger = new Swagger(basePath: basePath)
        GrailsControllerClass controllerClass = new DefaultGrailsControllerClass(TestController)
        service.grailsUrlService = Mock(GrailsUrlService)
        1 * service.grailsUrlService.getPathForAction(controllerClass, _) >> "/api/v1.0/unitTest/index"

        when:
        service.getPaths(swagger, [controllerClass])

        then:
        swagger.paths.keySet() == [expected] as Set

        where:
        basePath    || expected
        null        || "/api/v1.0/unitTest/index"
        "/api/v1.0" || "/unitTest/index"
    }
}

@Api(value="unitTest")
class TestController {

    @ApiResponses(value=[ @ApiResponse(code = 500, message = "Ups, we messed it up") ])
    def index(){}
}