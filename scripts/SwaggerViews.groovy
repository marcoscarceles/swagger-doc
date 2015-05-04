import org.apache.tools.ant.DirectoryScanner
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils

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
        String basePath = GrailsPluginUtils.getPluginDirForName('swagger-doc').file.absolutePath
        copy(todir: dir) {
            fileset(dir:"${basePath}/scripts/swagger-views/${dir}")
        }
        println "Done"
    }
}

setDefaultTarget(swaggerViews)
