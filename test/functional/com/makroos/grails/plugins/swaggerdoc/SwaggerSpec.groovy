package com.makroos.grails.plugins.swaggerdoc

import spock.lang.Specification

/**
 * Created by @marcos-carceles on 15/04/15.
 */
class SwaggerSpec extends Specification {

    def "swagger resource matches specification"() {
        when:
        String swaggerData = 'http://localhost:8080/endource/swagger.json'.toURL().text
        then:
        new SwaggerValidationService().validate(swaggerData)
    }
}
