package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PersonSourceSpec extends Specification implements DomainUnitTest<PersonSource> {

     void "test domain constraints"() {
        when:
        PersonSource domain = new PersonSource()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
