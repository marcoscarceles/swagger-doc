import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.util.Json
import grails.converters.JSON


class SwaggerBootStrap {

    def init = { servletContext ->
        JSON.registerObjectMarshaller(Swagger, { Swagger swagger ->
            JSON.parse(Json.mapper().writeValueAsString(swagger))
        })
    }
}
