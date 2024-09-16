package scott.mil

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ElasticServiceSpec extends Specification implements ServiceUnitTest<ElasticService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
