package scott.mil

import grails.core.GrailsApplication
import grails.gorm.transactions.ReadOnly
import javax.transaction.Transactional


class SourceController {
    def sourceService
    GrailsApplication grailsApplication

    @ReadOnly
    def index() {
        def sources = Source.list()
        respond sources
    }

    @Transactional
    def setNewSource () {
        def newSource = new Source(name: params.name, description: params.description, enabled: params.enabled, public1: params.public1)
        newSource.save(flush:true)
        sourceService.createElasticIndex(params.name)
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
        sourceService.deleteElasticIndex(source.name)
        respond {"Complete"}
    }

    @ReadOnly
    def getSources() {
        def sourceList = sourceService.getSources(params.person)
        respond sourceList
    }

    @ReadOnly
    def getSource() {
        def sourceList = sourceService.getSource(params.person)
        respond sourceList
    }
}