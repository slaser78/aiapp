package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonRoleSpec extends Specification implements DomainUnitTest<PersonRole> {

     void "test domain constraints"() {
        when:
        PersonRole domain = new PersonRole()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
