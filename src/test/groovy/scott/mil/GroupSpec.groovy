package scott.mil

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class GroupSpec extends Specification implements DomainUnitTest<Group> {

     void "test domain constraints"() {
        when:
        Group domain = new Group()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
