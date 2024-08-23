package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class FeedbackSpec extends Specification implements DomainUnitTest<Feedback> {

     void "test domain constraints"() {
        when:
        Feedback domain = new Feedback()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
