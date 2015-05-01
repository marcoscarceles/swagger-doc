import com.makroos.grails.plugins.swaggerdoc.helpers.MarshallerHelper
import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.Operation
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Response
import com.wordnik.swagger.models.Scheme
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.Tag
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition
import com.wordnik.swagger.models.parameters.Parameter
import com.wordnik.swagger.models.properties.Property
import grails.converters.JSON


class SwaggerBootStrap {

    def init = { servletContext ->
        [Swagger, Info, Tag, Path, Operation, Response, SecuritySchemeDefinition, Model, Property, Parameter, Scheme].each {
            JSON.registerObjectMarshaller(it,MarshallerHelper.MARSHALLER_WRAPPER)
        }
    }
}
