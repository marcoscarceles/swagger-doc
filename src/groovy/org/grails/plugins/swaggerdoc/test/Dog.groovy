package org.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.ApiModel

/**
 * Created by @marcos-carceles on 05/05/15.
 */
@ApiModel('Dog')
class Dog extends Pet {

    String favouriteToy
}
