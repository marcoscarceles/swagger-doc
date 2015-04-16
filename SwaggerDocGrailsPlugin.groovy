import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication

class SwaggerDocGrailsPlugin {
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.5 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Swagger Doc Plugin" // Headline display name of the plugin
    def author = "Marcos Carceles"
    def authorEmail = "marcos.carceles@gmail.com"
    def description = '''\
Swagger 2.0 implementation for Grails.
Based on https://github.com/swagger-api/swagger-core.
This plugin aims to be a non opinionated alternative to existing Swagger plugins for grails, \
making use of the bare swagger-core annotations without needing to use JAX-RS.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/marcos-carceles/swagger-doc/README.md"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Marcos Carceles", email: "marcos.carceles@gmail.com" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "Github", url: "https://github.com/marcos-carceles/swagger-doc/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/marcos-carceles/swagger-doc.git" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        mergeConfig(application)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
        mergeConfig(application)
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    private void mergeConfig(GrailsApplication grailsApplication) {
        ConfigObject swaggerAppConfig = grailsApplication.config.swagger
        ConfigSlurper slurper = new ConfigSlurper(Environment.getCurrent().getName());
        ConfigObject swaggerDefaultConfig = slurper.parse(grailsApplication.classLoader.loadClass("SwaggerConfig"))

        ConfigObject mergedConfig = new ConfigObject();
        mergedConfig.putAll(swaggerDefaultConfig.swagger.merge(swaggerAppConfig))

        grailsApplication.config.swagger = mergedConfig;
    }
}
