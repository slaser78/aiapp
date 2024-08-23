package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ChatControllerSpec extends Specification implements ControllerUnitTest<ChatController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
