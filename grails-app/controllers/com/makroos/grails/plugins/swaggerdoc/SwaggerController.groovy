package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.models.Swagger
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsApplication

class SwaggerController {

    GrailsApplication grailsApplication

    def index() {
        def swaggerConfig = grailsApplication.config.swagger
        println "SwaggerConfig is " + swaggerConfig
        def swagger = new Swagger(swaggerConfig)
        render swagger as JSON
    }
}
