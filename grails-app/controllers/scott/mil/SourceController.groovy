package scott.mil

import grails.core.GrailsApplication
import grails.gorm.transactions.ReadOnly
import groovy.json.JsonSlurper

import javax.transaction.Transactional


class SourceController {
    def sourceService
    GrailsApplication grailsApplication

    @ReadOnly
    def index() {
        respond Source.list()
    }

    @Transactional
    def save (Source source) {
        def newSource = sourceService.saveSource(source)
        respond newSource
    }

    @Transactional
    def update (Source source) {
        source.save(flush:true)
        respond source
    }

    @Transactional
    def delete(Source source) {
        log.warn("Delete user: ${source.name}")

        List <PersonSource> personSources = PersonSource.findAllWhere(source: source)
        if (personSources) {
            for (entry in personSources){
                entry.delete(flush:true)
            }
        }
        source.delete (flush:true)
        respond {"Complete"}
    }
}