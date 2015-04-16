import com.makroos.grails.plugins.swaggerdoc.marshaller.MarshallerHelper
import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Swagger
import grails.converters.JSON


class SwaggerDocBootStrap {

    def init = { servletContext ->
        [Swagger, Info].each {
            JSON.registerObjectMarshaller(it,MarshallerHelper.MARSHALLER_WRAPPER)
        }
    }
}
