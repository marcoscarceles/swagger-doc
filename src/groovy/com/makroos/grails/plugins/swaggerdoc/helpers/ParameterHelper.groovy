package com.makroos.grails.plugins.swaggerdoc.helpers

import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.models.parameters.BodyParameter
import com.wordnik.swagger.models.parameters.CookieParameter
import com.wordnik.swagger.models.parameters.FormParameter
import com.wordnik.swagger.models.parameters.HeaderParameter
import com.wordnik.swagger.models.parameters.Parameter
import com.wordnik.swagger.models.parameters.PathParameter
import com.wordnik.swagger.models.parameters.QueryParameter

import java.lang.reflect.Method

/**
 * Created by @marcos-carceles on 30/04/15.
 */
class ParameterHelper {

    static Parameter getParameterFor(ApiParam apiParam, Method action, String pathStr) {
        String paramType = pathStr.contains(apiParam.name()) ? 'path' : 'query'
        Class dataType = action.parameterTypes ? action.parameterTypes[0] : String
        buildParameter(apiParam.name(), apiParam.required(), paramType, dataType)
    }

    static Parameter getParameterFor(ApiImplicitParam apiParam, String pathStr) {
        String paramType = apiParam.paramType() ?: pathStr.contains(apiParam.name()) ? 'path' : 'query'
        String dataType = apiParam.dataType() ?: 'string'
        buildParameter(apiParam.name(), apiParam.required(), paramType, dataType)
    }

    private static Parameter buildParameter(String name, boolean required, String paramType, def dataType) {
        Parameter parameter = getParameterofType(paramType)
        parameter.name = name
        parameter.required = required
        if(parameter.hasProperty('type')) {
            Map typeFormat = PropertyHelper.getTypeFormatFor(dataType ?: 'string')
            parameter.type = typeFormat.type
            parameter.format = typeFormat.format
        }
        parameter
    }

    static Parameter getParameterofType(String paramType) {
        switch(paramType) {
            case 'body': return new BodyParameter()
            case 'cookie': return new CookieParameter()
            case 'formData': return new FormParameter()
            case 'header': return new HeaderParameter()
            case 'path': return new PathParameter()
            case 'query': return new QueryParameter()
            default: throw new Exception("paramType must be one of ['body','cookie','formData','header','path','query']")
        }
    }
}
