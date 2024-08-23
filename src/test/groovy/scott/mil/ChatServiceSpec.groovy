package scott.mil

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ChatServiceSpec extends Specification implements ServiceUnitTest<ChatService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
