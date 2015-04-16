package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiParam
import grails.test.mixin.TestFor
import spock.lang.Specification

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GrailsUrlService)
class GrailsUrlServiceSpec extends Specification {

    void "can infer the action parameters"() {
        given:
        Method action = GrailsUrlServiceSpec.class.getDeclaredMethod('sampleAction',String, String)

        when:
        def params = service.getParameters(action)

        then:
        params*.name() == ['id','slug','prettyprint','format','cached']
    }

    @ApiImplicitParams(value = [
            @ApiImplicitParam(name="format"),
            @ApiImplicitParam(name="cached")
    ])
    @ApiParam(name="prettyprint")
    private def sampleAction(
            @ApiParam(name="id", required = true) String id,
            @ApiParam(name="slug", required = true) String slug
    ) {

    }
}

