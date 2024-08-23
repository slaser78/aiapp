package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class TopicControllerSpec extends Specification implements ControllerUnitTest<TopicController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
