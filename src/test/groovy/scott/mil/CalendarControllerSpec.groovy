package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class CalendarControllerSpec extends Specification implements ControllerUnitTest<CalendarController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
