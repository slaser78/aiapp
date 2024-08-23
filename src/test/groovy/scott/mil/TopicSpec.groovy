package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TopicSpec extends Specification implements DomainUnitTest<Topic> {

     void "test domain constraints"() {
        when:
        Topic domain = new Topic()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
