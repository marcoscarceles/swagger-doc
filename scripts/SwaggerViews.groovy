import org.apache.tools.ant.DirectoryScanner

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

includeTargets << grailsScript("_GrailsInit")

target(swaggerViews: "Creates customizable Swagger-UI views") {
    List<String> swaggerUIDirs = [ 'grails-app/assets/images/swagger',
                                   'grails-app/assets/javascripts/swagger',
                                   'grails-app/assets/stylesheets/swagger',
                                   'grails-app/views/swagger',
                                   'grails-app/views/layouts'
    ]
    swaggerUIDirs.each { dir ->
        println "Copying files to ${dir} ..."
        new File(dir).mkdirs()
        copy(todir: dir) {
            fileset(dir:"scripts/swagger-views/${dir}")
        }
        println "Done"
    }
}

setDefaultTarget(swaggerViews)
