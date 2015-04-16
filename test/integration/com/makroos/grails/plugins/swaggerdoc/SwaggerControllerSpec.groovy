package com.makroos.grails.plugins.swaggerdoc

import grails.util.Metadata
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 16/04/15.
 */
class SwaggerControllerSpec  extends Specification{

    SwaggerController controller
    GrailsApplication grailsApplication

    def setup() {
        controller = new SwaggerController()
    }

    void "index renders swagger info from configuration"() {
        when:
        controller.index()

        then:
        def json = controller.response.json
        json.info.title == Metadata.current['app.name']
    }
}
