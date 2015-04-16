import com.makroos.grails.plugins.swaggerdoc.marshaller.MarshallerHelper
import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import grails.converters.JSON


class SwaggerBootStrap {

    def init = { servletContext ->
        [Swagger, Info, Tag].each {
            JSON.registerObjectMarshaller(it,MarshallerHelper.MARSHALLER_WRAPPER)
        }
    }
}
