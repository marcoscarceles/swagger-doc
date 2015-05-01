package com.makroos.grails.plugins.swaggerdoc.helpers

import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponses
import org.codehaus.groovy.grails.commons.GrailsControllerClass

import java.lang.reflect.Method

/**
 * Created by @marcos-carceles on 01/05/15.
 */
class GrailsControllerHelper {

    /**
     * Given a list of action candidate Methods, process them trying to identify which ones correspond to the same
     * API Endpoint and returning a subset of unique methods
     * @param actionCandidates
     * @return
     */
    static List<Method> getApiActions(List<Method> actionCandidates) {
        Map<String, Method> actionMap = [:]
        actionCandidates.each { Method action ->
            if(!actionMap.containsKey(action.name)) {
                actionMap.put(action.name,action)
            } else {
                Method previousAction = actionMap.get(action.name)
                if(previousAction.parameterTypes.length < action.parameterTypes.length) {
                    actionMap.put(action.name, action)
                }
            }
        }
        actionMap.values() as List
    }

    static List<Method> getApiActions(GrailsControllerClass grailsController) {
        List<Method> actionCandidates = grailsController.clazz.methods.findAll {
            it.getAnnotation(ApiOperation) || it.getAnnotation(ApiResponses) || it.getAnnotation(ApiParam) || it.getAnnotation(ApiImplicitParams)
        }
        getApiActions(actionCandidates)
    }
}
