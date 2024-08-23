package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class CalendarSpec extends Specification implements DomainUnitTest<Calendar> {

     void "test domain constraints"() {
        when:
        Calendar domain = new Calendar()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
