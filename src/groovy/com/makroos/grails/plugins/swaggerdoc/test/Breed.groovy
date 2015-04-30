package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty

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
    @ApiModelProperty(dataType = 'string')
    Set<String> subspecies
}
