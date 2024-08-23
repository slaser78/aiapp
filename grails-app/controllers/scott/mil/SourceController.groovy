package scott.mil

import grails.gorm.transactions.ReadOnly
import javax.transaction.Transactional

class SourceController {
    def sourceService
    @ReadOnly
    def index() {
        respond Source.list()
    }

    @Transactional
    def save (Source source) {
        def newSource = new Source(name: source.name, description: source.description, enabled: source.enabled, public1: source.public1)
        newSource.save(flush:true)
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