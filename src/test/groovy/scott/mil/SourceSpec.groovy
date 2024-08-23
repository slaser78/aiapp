package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class SourceSpec extends Specification implements DomainUnitTest<Source> {

     void "test domain constraints"() {
        when:
        Source domain = new Source()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
