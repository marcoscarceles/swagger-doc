package com.makroos.grails.plugins.swaggerdoc.marshaller

import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import grails.converters.JSON
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 16/04/15.
 */
class MarshallersSpec extends Specification {

    void "marshalled objects must not contain class data"() {
        when:
        List<JSON> jsonObjects = [Swagger, Info, Tag].collect { Class it ->
            it.newInstance() as JSON
        }

        then:
        jsonObjects.every {
            !(it as String =~ /"?class"?:/)
        }
    }

    void "info JSON does not include vendorExtensions"() {
        when:
        JSON infoJson = new Info() as JSON
        then:
        !(infoJson as String =~ /"?vendorExtensions"?:/)
    }
}
