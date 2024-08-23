package scott.mil

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class PersistentChatMemoryStoreServiceSpec extends Specification implements ServiceUnitTest<PersistentChatMemoryStoreService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
