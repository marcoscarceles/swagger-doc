package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
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
        render([
            new Pet(name: "Tod", collarNumber: 42),
            new Pet(name: "Toby", collarNumber: 24)
        ] as JSON)
    }

    @ApiResponses(value = [
            @ApiResponse(code=404,message = "Pet Not Found"),
            @ApiResponse(code=410,message = "Pet Gone")
    ])
    @ApiParam(name="id")
    def show(Integer id) {
        render new Pet(name:"Rudolph", collarNumber: 1) as JSON
    }

}
