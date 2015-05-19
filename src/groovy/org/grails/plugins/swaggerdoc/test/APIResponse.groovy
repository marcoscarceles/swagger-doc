package org.grails.plugins.swaggerdoc.test

/**
 * Created by @marcos-carceles on 30/04/15.
 */
class APIResponse {

    Code code
    String message
    def meta
}

enum Code {
    SUCCESS, ERROR, PROCESSING
}
