package scott.mil

import grails.gorm.transactions.ReadOnly

import javax.transaction.Transactional


class PersonSourceController {
    def sourceService
    @ReadOnly
    def getPersonSource() {
        Long personLong = Long.valueOf(params.person)
        Person person = Person.findWhere(id: personLong)
        List <PersonSource> personSources = PersonSource.findAllWhere(person: person)
        List personSourceList = []
        if (personSources){
            for (personSource in personSources){
                def personMap = ["name": personSource.source.name ]
                personSourceList.add(personMap)
            }
        }
        respond (personSourceList)
    }

    @Transactional
    def setSource() {
        sourceService.setSource(params.sources, params.name)
        respond "Complete"
    }

    @ReadOnly
    def index() {
        def personList = PersonSource.list()
        def personSourceList = []
        for (entry in personList){
            Map idMap = ["id": entry.id]
            Map personMap = ["person": entry.person.name]
            Map sourceMap = ["source": entry.source.name]
            personSourceList += idMap + personMap + sourceMap
        }
        respond personSourceList
    }
    @Transactional
    def addPersonsToSource() {
        sourceService.addPersonsToSource(params.persons, params.source)
        respond "Complete"
    }

    @Transactional
    def addSourcesToPerson() {
        sourceService.addSourcesToPerson(params.person, params.sources)
        respond "Complete"
    }
}