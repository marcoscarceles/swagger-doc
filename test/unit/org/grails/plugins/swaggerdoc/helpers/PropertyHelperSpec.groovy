package org.grails.plugins.swaggerdoc.helpers

import org.grails.plugins.swaggerdoc.test.Breed
import org.grails.plugins.swaggerdoc.test.Pet
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by @marcos-carceles on 30/04/15.
 */
@Unroll
class PropertyHelperSpec extends Specification {

    def "generates the correct datatype #expected for class #clazz"() {
        expect:
        PropertyHelper.getDatatypeFor(clazz) == expected
        where:
        clazz   || expected
        String  || 'string'
        Date    || 'date'
        int     || 'integer'
        Integer || 'integer'
        long    || 'long'
        Long    || 'long'
        double  || 'double'
        Double  || 'double'
        float   || 'float'
        Float   || 'float'
        boolean || 'boolean'
        Boolean || 'boolean'
    }

    def "generates datatypes for complex objects"() {
        expect: 'complex types'
        PropertyHelper.getDatatypeFor(Pet) == 'complex'
        PropertyHelper.getDatatypeFor(Breed) == 'complex'
        and: 'a non valid, bu needed object'
        PropertyHelper.getDatatypeFor(Object) == 'object'
    }

    def "retrieves the swagger type and format #expected for datatype #datatype "() {
        expect:
        PropertyHelper.getTypeFormatFor(datatype) == expected
        where:
        datatype  || expected
        'string'  || [type: 'string']
        'date'    || [type: 'string', format: 'date']
        'integer' || [type: 'integer', format: 'int32']
        'long'    || [type: 'integer', format: 'int64']
        'double'  || [type: 'number', format: 'double']
        'float'   || [type: 'number', format: 'float']
        'boolean' || [type: 'boolean']
        'complex' || [type: '$ref']
        'object'  || [type: 'object']
    }
}
