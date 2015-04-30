grails.plugins.swaggerdoc = [
        swagger: {
            info {
                version = "1.0.0"
                title = grails.util.Metadata.current['app.name']
            }
            basePath = "/api"
            paths = [:]
            schemes = ["http"]
        }
]