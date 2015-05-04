import com.wordnik.swagger.models.Scheme
import org.springframework.http.HttpStatus

swaggerdoc {
    swagger {
        info {
            version = '1.0.0'
            title = grails.util.Metadata.current['app.name']
        }
        basePath = '/api'
        paths = [:]
        schemes = [Scheme.forValue('http')]
    }
    defaults {
        responses = [
            200: 'OK',
            404: 'Not Found'
        ]
    }
}