package com.makroos.grails.plugins.swaggerdoc

import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiParam

import java.lang.annotation.Annotation
import java.lang.reflect.Method

class GrailsUrlService {

    List<ApiParam> getParameters(Method action)  {
        List<Annotation> annotations = (action.parameterAnnotations as List).flatten()
        annotations = annotations.flatten()
        annotations += action.getAnnotation(ApiParam)
        annotations += action.getAnnotation(ApiImplicitParams)?.value() as List
        annotations.findAll { it?.annotationType() in [ApiParam, ApiImplicitParam] }
    }

}
