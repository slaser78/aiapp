package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class FeedbackControllerSpec extends Specification implements ControllerUnitTest<FeedbackController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
