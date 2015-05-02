package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.models.Swagger
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsApplication

class SwaggerController {

    static namespace = "swagger-doc"

    GrailsApplication grailsApplication
    SwaggerService swaggerService
    SwaggerValidationService swaggerValidationService

    def index() {
        render view: 'index'
    }

    def swagger() {
        Map swaggerConfig = grailsApplication.config.swaggerdoc.swagger as Map
        def swagger = new Swagger(swaggerConfig)
        swaggerService.getTags(swagger)
        swaggerService.getPaths(swagger)
        swaggerService.getSecurityDefinitions(swagger)
        render new JSON(swagger)
    }

    def validate() {
        String swaggerDoc = g.createLink(namespace:'swagger-doc', controller:'swagger', action:'swagger', absolute: true).toURL().text
        render swaggerValidationService.validate(swaggerDoc)
    }
}
