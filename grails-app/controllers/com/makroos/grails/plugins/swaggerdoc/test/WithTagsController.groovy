package com.makroos.grails.plugins.swaggerdoc.test

import com.wordnik.swagger.annotations.Api

/**
 * Created by @marcos-carceles on 16/04/15.
 */
@Api(
        value="ignore_this",
        tags = ["with_tag", "with_another_tag"],
        description = "When Api declares tags, it overrrides the description"
)
class WithTagsController {
}
