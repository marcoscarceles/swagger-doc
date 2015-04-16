package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.Authorization
import grails.converters.JSON

/**
 * Created by @marcos-carceles on 16/04/15.
 */
@Api(value = "pet",
        description = "Pet operations",
        authorizations = [
        @Authorization(value="petauth", type="basic")
    ]
//        , basePath = "No longer user in swagger-core 1.5.x"
//        , position = "No longer user in swagger-core 1.5.x"
)
class PetController {

    def index() {
        render new Pet(name: "Toby", collarNumber: 42) as JSON
    }

}
