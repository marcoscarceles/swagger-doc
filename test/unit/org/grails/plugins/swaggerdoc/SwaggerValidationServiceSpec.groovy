package org.grails.plugins.swaggerdoc

import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 19/05/15.
 */
@TestFor(SwaggerValidationService)
class SwaggerValidationServiceSpec extends Specification {

    String petStoreJson

    def setup() {
        petStoreJson = "http://petstore.swagger.io/v2/swagger.json".toURL().text
    }

    def "validates valid Swagger JSON"() {
        expect:
        service.validate(petStoreJson) == true
    }

    def "rejects invalid Swagger JSON"() {
        expect:
        service.validate(petStoreJson.replaceFirst('"swagger":"2.0"','"swagger":"1.2"')) == false
        service.validate(petStoreJson.replaceFirst('"oauth2"','"bespokeauth"')) == false
    }
}
