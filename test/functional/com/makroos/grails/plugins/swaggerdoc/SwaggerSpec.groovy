package com.makroos.grails.plugins.swaggerdoc

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 15/04/15.
 */
class SwaggerSpec extends Specification {

    private static final URL SWAGGER_SCHEMA = 'https://raw.githubusercontent.com/swagger-api/swagger-spec/master/schemas/v2.0/schema.json'.toURL()

    def "swagger resource matches specification"() {
        when:
        String swaggerData = 'http://localhost:8080/swagger-doc/swagger/index'.toURL().text
        then:
        validate(swaggerData)
    }

    private boolean validate(String dataString) throws Exception {
        // create the Json nodes for schema and data
        JsonNode schemaNode = JsonLoader.fromURL(SWAGGER_SCHEMA)
        JsonNode dataNode = JsonLoader.fromString(dataString)

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault()
        // load the schema and validate
        JsonSchema schema = factory.getJsonSchema(schemaNode)
        ProcessingReport report = schema.validate(dataNode)

        report.each { ProcessingMessage it ->
            if (it.logLevel == LogLevel.WARNING) {
              log.warn(it)
            } else if(it.logLevel == LogLevel.ERROR) {
              log.error(it)
            }
        }

        return report.isSuccess()
    }
}
