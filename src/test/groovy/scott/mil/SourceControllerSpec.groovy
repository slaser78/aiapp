package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SourceControllerSpec extends Specification implements ControllerUnitTest<SourceController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
