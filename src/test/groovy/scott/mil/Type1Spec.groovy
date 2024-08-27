package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class Type1Spec extends Specification implements DomainUnitTest<Type1> {

     void "test domain constraints"() {
        when:
        Type1 domain = new Type1()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
