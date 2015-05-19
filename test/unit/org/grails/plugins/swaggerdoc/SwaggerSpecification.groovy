package org.grails.plugins.swaggerdoc

import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.grails.plugins.swaggerdoc.test.PetController
import org.grails.plugins.swaggerdoc.test.WithTagsController
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by @marcos-carceles on 21/04/15.
 */
abstract class SwaggerSpecification extends Specification {

    @Shared List<GrailsControllerClass> appControllers
    @Shared GrailsControllerClass petController
    @Shared GrailsControllerClass tagsController



    def setupSpec() {
        appControllers = []
        petController = new DefaultGrailsControllerClass(PetController)
        appControllers << petController
        tagsController = new DefaultGrailsControllerClass(WithTagsController)
        appControllers << tagsController
    }
}
