package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.ApiModel

/**
 * Created by @marcos-carceles on 22/04/15.
 */
@ApiModel(
        value="PetBreed",
        description="Type of Pet"
)
class Breed {

    String name
    String scientificName
    String description
}
