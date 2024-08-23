package scott.mil

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class DocumentServiceSpec extends Specification implements ServiceUnitTest<DocumentService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
