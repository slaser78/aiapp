package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ChatSpec extends Specification implements DomainUnitTest<Chat> {

     void "test domain constraints"() {
        when:
        Chat domain = new Chat()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
