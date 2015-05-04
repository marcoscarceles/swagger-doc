import java.nio.file.Files
import java.nio.file.Paths

includeTargets << grailsScript("_GrailsInit")

target(swaggerViews: "Creates customizable Swagger-UI views") {
    List<String> swaggerUIDirs = [ 'grails-app/assets/images/swagger',
                                   'grails-app/assets/javascripts/swagger',
                                   'grails-app/assets/stylesheets/swagger',
                                   'grails-app/views/swagger',
                                   'grails-app/layouts'
    ]
    swaggerUIDirs.each {
        new File(it).mkdirs()
        Files.copy(Paths.get("scripts/swagger-views/${it}/*"), Paths.get(it))

    }
}

setDefaultTarget(swaggerViews)
