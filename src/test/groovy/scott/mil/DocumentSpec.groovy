package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class DocumentSpec extends Specification implements DomainUnitTest<Document> {

     void "test domain constraints"() {
        when:
        Document domain = new Document()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
