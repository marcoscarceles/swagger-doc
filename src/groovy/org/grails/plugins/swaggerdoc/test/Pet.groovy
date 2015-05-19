package org.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.ApiModelProperty

/**
 * Created by @marcos-carceles on 16/04/15.
 */
class Pet {
    String name
    @ApiModelProperty(required = true)
    int collarNumber
    Breed breed
    Date dateOfBirth
    @ApiModelProperty(dataType = 'string')
    List<String> allergies
    @ApiModelProperty(hidden = true)
    boolean bittenSomeone
}
