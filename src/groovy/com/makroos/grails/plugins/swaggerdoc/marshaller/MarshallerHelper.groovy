package com.makroos.grails.plugins.swaggerdoc.marshaller

import com.wordnik.swagger.models.Path
import com.wordnik.swagger.models.Swagger

/**
 * Created by @marcos-carceles on 15/04/15.
 */
class MarshallerHelper {

    private static final Closure DEFAULT_JSON_MARSHALLER = { def it ->
        Map json = (it.properties - [class: it.class]).findAll {k, v ->
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

    public static final MARSHALLER_WRAPPER = { def it ->
        switch(it.class) {
            case Swagger: return SWAGGER_MARSHALLER.call(it)
            case Path: return PATH_MARSHALLER.call(it)
            default: return DEFAULT_JSON_MARSHALLER.call(it)
        }
    }
}
