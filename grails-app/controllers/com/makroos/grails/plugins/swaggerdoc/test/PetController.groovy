package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
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

    @ApiOperation(
            value = "Lists pets",
            notes = "This is an indepth, longer that 120 characters description of what /pet/index does, which is just listing pets, really, nothing more, nothing less",
            response = Pet,
            responseContainer = "array"
    )
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
    @ApiParam(name="id", required=true)
    def show(Long id) {
        render new Pet(name:"Rudolph", collarNumber: 1) as JSON
    }

    @ApiImplicitParams([@ApiImplicitParam(name="id",required=true,dataType = "integer", paramType="path")])
    def buy(Integer id) {
        render new Pet(name:"Rudolph", collarNumber: 1) as JSON
    }

    @ApiOperation(
            value = "Add a pet the store",
            response = APIResponse
    )
    @ApiResponses([@ApiResponse(code=200,message = 'successfully saved')])
    def save() {

    }

}
