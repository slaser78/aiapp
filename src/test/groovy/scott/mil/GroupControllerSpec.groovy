package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class GroupControllerSpec extends Specification implements ControllerUnitTest<GroupController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
