package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.models.Swagger
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsApplication

class SwaggerController {

    GrailsApplication grailsApplication
    SwaggerService swaggerService

    def index() {
        Map swaggerConfig = grailsApplication.config.swagger as Map
        def swagger = new Swagger(swaggerConfig)
        swaggerService.getTags(swagger)
        swaggerService.getPaths(swagger)
        swaggerService.getSecurityDefinitions(swagger)
        render new JSON(swagger)
    }
}
