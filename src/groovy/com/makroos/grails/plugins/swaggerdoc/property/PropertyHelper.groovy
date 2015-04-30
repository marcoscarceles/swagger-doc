package com.makroos.grails.plugins.swaggerdoc.property

import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import com.wordnik.swagger.models.properties.ArrayProperty
import com.wordnik.swagger.models.properties.Property
import com.wordnik.swagger.models.properties.PropertyBuilder
import com.wordnik.swagger.models.properties.RefProperty
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.lang.reflect.Field

/**
 * Created by @marcos-carceles on 22/04/15.
 */
class PropertyHelper {

    private static final Log log = LogFactory.getLog(PropertyHelper)

    static String getDatatypeFor(Class clazz) {
        if(clazz in [Boolean, boolean, Date, Double, double, Float, float, Integer, Long, long, String, Object]) {
            return clazz.simpleName.toLowerCase()
        } else if (clazz == int) {
            return 'integer'
        } else if (Enum.isAssignableFrom(clazz)) {
            return 'string'
        }
        else {
            return 'complex'
        }
    }

    static Map<String,String> getTypeFormatFor(String datatype) {
        switch(datatype) {
            case "integer":
                return [type: "integer", format: "int32"]
            case "long":
                return [type: "integer", format: "int64"]
            case "float":
                return [type: "number", format: "float"]
            case "double":
                return [type: "number", format: "double"]
            case "string":
                return [type: "string"]
            case "byte":
                return [type: "string", format: "byte"]
            case "boolean":
                return [type: "boolean"]
            case "date":
                return [type: "string", format: "date"]
            case "dateTime":
                return [type: "string", format: "date-time"]
            case "complex":
                return [type: '$ref']
            case "object":
                return [type: 'object']
            case "null":
            case "":
            case null:
                return null
            default:
                throw new Exception("Unknown datatype ${datatype}. Mulst be one one of the types defined in https://github.com/swagger-api/swagger-core/wiki/Datatypes")
        }
    }

    static Property buildPropertyFor(Field field) {
        log.debug("Building property for field ${field.name} of type ${field.type}")
        ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty)
        Class clazz = field.type

        def datatype = modelProperty?.dataType() ?: getDatatypeFor(clazz)
        def typeFormat = getTypeFormatFor(datatype)
        Map propertyArgs = [:]
        if(Enum.isAssignableFrom(clazz)) {
            propertyArgs['enum'] = (clazz).values()*.toString() as List
        }
        Property property = PropertyBuilder.build(typeFormat.type,typeFormat.format, propertyArgs)

        if(List.isAssignableFrom(clazz) || Set.isAssignableFrom(clazz)) {
            ArrayProperty arrayProperty = new ArrayProperty(property)
            arrayProperty.uniqueItems == Set.isAssignableFrom(clazz)
            property = arrayProperty
        }

        if (property instanceof RefProperty) {
            property.$ref = field.type.getAnnotation(ApiModel)?.value() ?: field.type.simpleName
        }

        property.title = modelProperty?.name() ?: field.name
        property.description = modelProperty?.value() ?: null
        property.required = modelProperty?.required() ?: false

        return property
    }
}
