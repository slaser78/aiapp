package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class PersonControllerSpec extends Specification implements ControllerUnitTest<PersonController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
