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

    void "index renders the tags existing in the application"() {
        when:
        controller.index()

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
        controller.index()
        then:
        def json = controller.response.json
        json.securityDefinitions['petauth'] == [type: 'basic']
    }
}
