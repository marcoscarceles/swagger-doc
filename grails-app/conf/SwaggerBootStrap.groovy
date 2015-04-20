import com.makroos.grails.plugins.swaggerdoc.marshaller.MarshallerHelper
import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Operation
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Response
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import grails.converters.JSON


class SwaggerBootStrap {

    def init = { servletContext ->
        [Swagger, Info, Tag, Path, Operation, Response, SecuritySchemeDefinition].each {
            JSON.registerObjectMarshaller(it,MarshallerHelper.MARSHALLER_WRAPPER)
        }
    }
}
