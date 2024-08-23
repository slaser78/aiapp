package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonSpec extends Specification implements DomainUnitTest<Person> {

     void "test domain constraints"() {
        when:
        Person domain = new Person()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
