package com.makroos.grails.plugins.swaggerdoc.marshaller

import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.Swagger

/**
 * Created by @marcos-carceles on 15/04/15.
 */
class MarshallerHelper {

    private static final Closure DEFAULT_JSON_MARSHALLER = { def it ->
        (it.properties - [class: it.class]).findAll {k, v ->
            v != null
        }
    }

    private static final Closure SWAGGER_MARSHALLER = { def it ->
        DEFAULT_JSON_MARSHALLER.call(it) - [securityRequirement : null]
    }

    private static final Closure INFO_MARSHALLER = { def it ->
        Map json = DEFAULT_JSON_MARSHALLER.call(it)
        json.remove('vendorExtensions')
        json
    }

    public static final MARSHALLER_WRAPPER = { def it ->
        switch(it.class) {
            case Swagger: return SWAGGER_MARSHALLER.call(it)
            case Info: return INFO_MARSHALLER.call(it)
            default: return DEFAULT_JSON_MARSHALLER.call(it)
        }
    }
}
