package com.makroos.grails.plugins.swaggerdoc.marshaller

import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Swagger
import com.wordnik.swagger.models.properties.Property
import com.wordnik.swagger.models.properties.RefProperty
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Created by @marcos-carceles on 15/04/15.
 */
//TODO: Replace with Jackson marshaller
class MarshallerHelper {

    private static final Closure DEFAULT_JSON_MARSHALLER = { def it ->
        //Needed to explicitly use DefaultGroovyMethods, as .getProperties() is defined in some classes (like Model)
        Map json = (DefaultGroovyMethods.getProperties(it) - [class: it.class]).findAll {k, v ->
            v != null
        }
        json.remove('vendorExtensions')
        json
    }

    private static final Closure SWAGGER_MARSHALLER = { Swagger it ->
        DEFAULT_JSON_MARSHALLER.call(it) - [securityRequirement : null]
    }

    private static final Closure PATH_MARSHALLER = { Path it ->
        Map json = DEFAULT_JSON_MARSHALLER.call(it)
        json.remove('empty')
        json.remove('operations')
        json
    }

    private static final Closure MODEL_MARSHALLER = { Model it ->
        Map json = DEFAULT_JSON_MARSHALLER.call(it)
        json.remove('simple')
        json.remove('name')
        List<String> requiredFields = it.properties.findAll { it.value.required }.values()*.title
        if (requiredFields) {
            json.put('required', requiredFields)
        }
        json
    }

    private static final Closure REF_MARSHALLER = { RefProperty it ->
        ['$ref':it.$ref]
    }

    private static final Closure PROPERTY_MARSHALLER = { Property it ->
        Map json = DEFAULT_JSON_MARSHALLER.call(it)
        json.remove('title')
        json.remove('required')
        json
    }

    public static final MARSHALLER_WRAPPER = { def it ->
        switch(true) {
            case Swagger.isAssignableFrom(it.class): return SWAGGER_MARSHALLER.call(it)
            case Path.isAssignableFrom(it.class): return PATH_MARSHALLER.call(it)
            case Model.isAssignableFrom(it.class) : return MODEL_MARSHALLER.call(it)
            case RefProperty.isAssignableFrom(it.class) : return REF_MARSHALLER.call(it)
            case Property.isAssignableFrom(it.class) : return PROPERTY_MARSHALLER.call(it)
            default: return DEFAULT_JSON_MARSHALLER.call(it)
        }
    }
}
