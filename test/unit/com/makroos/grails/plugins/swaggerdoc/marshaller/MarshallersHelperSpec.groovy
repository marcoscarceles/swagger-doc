package com.makroos.grails.plugins.swaggerdoc.marshaller

import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import grails.converters.JSON
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 16/04/15.
 */
class MarshallersHelperSpec extends Specification {

    void "marshalled objects must not contain class data"() {
        when:
        List<Map> jsonObjects = [Swagger, Info, Tag, Path].collect { Class it ->
            MarshallerHelper.MARSHALLER_WRAPPER(it.newInstance())
        }

        then:
        jsonObjects.every {
            !it.containsKey('class')
            !(it as String =~ /"?class"?:/)
        }
    }

    void "info JSON does not include vendorExtensions"() {
        when:
        Map infoJson = MarshallerHelper.MARSHALLER_WRAPPER(new Info())
        then:
        !(infoJson as String =~ /"?vendorExtensions"?:/)
    }
}
