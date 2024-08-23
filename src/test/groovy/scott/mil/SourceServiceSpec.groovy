package scott.mil

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SourceServiceSpec extends Specification implements ServiceUnitTest<SourceService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
