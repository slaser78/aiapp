package scott.mil

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject


@Transactional
class PersonController {

    def sourceService

    @ReadOnly
    def index() {
        respond Person.list()
    }

    def show(Person person) {
        respond person
    }

    @Transactional
    def save(Person person) {
        person.save()
        respond person
    }

    @Transactional
    def update(Person person) {
        person.save()
        respond person
    }

    @Transactional
    def delete(Person person) {
        log.warn("Delete person: ${person.name}")
        def personSources = PersonSource.findAllWhere(person: person)
        if (personSources) {
            for (entry in personSources)
                entry.delete(flush:true)
        }
        person.delete()
        redirect action: "index", method: "GET"
    }

    @Transactional
    def usernameGetInitial () {
        Enumeration headerNames = request.getHeaderNames()
        String user = "scott"
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement()
            if (key == "oidc_claim_email") {
                user = request.getHeader(key)
            }
        }
        log.warn ("User: " + user)
        Person personValue = Person.findWhere(name: user)
        if (personValue) {
            LinkedHashMap<String, String> personMap = ["username": user]
            log.warn ("Role: ${personValue.role.role}")
            def roleMap = ["role": personValue.role.role]
            personValue.save()
            def initialValues = personMap + roleMap
            respond(initialValues)
        } else {
            log.error ("No user name found in CVMT for user ${user}")
            redirect (url: "https://sso-dev.jten.mil")
        }
    }

    def getPersonSources() {
        def sources = sourceService.getPersonSources(params.person)
        respond sources
    }
}