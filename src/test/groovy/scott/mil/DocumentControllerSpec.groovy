package scott.mil

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class DocumentControllerSpec extends Specification implements ControllerUnitTest<DocumentController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
