package org.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiParam
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import java.lang.annotation.Annotation
import java.lang.reflect.Method

class GrailsUrlService {

    LinkGenerator grailsLinkGenerator

    String getPathForAction(GrailsControllerClass grailsController, Method action) {

        String controllerName = grailsController.logicalPropertyName

        List<String> parameterNames = getParameterNames(action)
        Map queryPameters = parameterNames.collectEntries { [(it): "{$it}"] }
        Map linkData = [
                controller: controllerName,
                action: action.name,
                params: queryPameters
        ]
        if(grailsController.namespace) {
            linkData << [namespace: grailsController.namespace]
        }

        String grailsLink = grailsLinkGenerator.link(linkData).decodeURL()
        grailsLink.split(/\?/)[0]
    }

    List<Annotation> getParameters(Method action)  {
        List<Annotation> annotations = (action.parameterAnnotations as List).flatten()
        annotations = annotations.flatten()
        annotations += action.getAnnotation(ApiParam)
        annotations += action.getAnnotation(ApiImplicitParams)?.value() as List
        annotations.findAll { it?.annotationType() in [ApiParam, ApiImplicitParam] }
    }

    List<String> getParameterNames(Method action) {
        getParameters(action)*.name()
    }
}
