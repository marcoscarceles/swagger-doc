package com.makroos.grails.plugins.swaggerdoc.helpers

import com.makroos.grails.plugins.swaggerdoc.test.PetController
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created by @marcos-carceles on 01/05/15.
 */
class GrailsControllerHelperSpec extends Specification {


    def "can determine the actions for a given GrailsController"() {
        given:
        GrailsControllerClass grailsController = new DefaultGrailsControllerClass(PetController)
        expect: "Strange (but true), given that there's only one declared"
        grailsController.clazz.methods.findAll { it.name == 'show' }.size() == 2
        grailsController.clazz.methods.findAll { it.name == 'buy' }.size() == 2

        when:
        List<Method> apiActions = GrailsControllerHelper.getApiActions(grailsController)

        then:
        apiActions.findAll { it.name == 'show'}.size() == 1
        apiActions.find { it.name == 'show'}.parameterTypes == [Long]
        apiActions.findAll { it.name == 'buy'}.size() == 1
        apiActions.find { it.name == 'buy'}.parameterTypes == [Integer]
    }

}
