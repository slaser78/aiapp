package scott.mil

import groovy.transform.CompileStatic

import javax.transaction.Transactional

@CompileStatic
class SourceService {
     def setSource(String sources, String name) {
        //split returned sources values into each source
        def sources1 = sources.split(",")
        Person person = Person.findWhere(name: name)
         //List of all the current personSource entries for a person
        List personSources = PersonSource.findAllWhere(person: person)
        if (sources1) {
             for (sourceEntry in sources1) {
                 //get source for source string
                 Source source = Source.findWhere(name: sourceEntry)
                 PersonSource personSource2 = PersonSource.findWhere(source: source, person: person)
                 //check to see if the person's sources contains the returned source value
                 if (!personSource2) {
                     //if person source doesn't exist, add it
                     new PersonSource(person: person, source: source).save(flush:true)
                 } else {
                     //remove existing entry from personSources list
                     personSources.remove(personSource2)
                 }
             }
             //if any personSources list items remain, delete them
             if (personSources) {
                 personSources.each { entry1 ->
                     entry1.delete(flush:true)
                 }
             }
        }
    }

    @Transactional
    def addSourcesToPerson(def sources, def person){
        Person person1 = Person.findWhere(name: person)
        if (sources) {
            for (source in sources){
                Source source1 = Source.findWhere(name: source)
                PersonSource personSource = PersonSource.findWhere(person: person1, source: source1)
                if (!personSource){
                    new PersonSource(person: person1, source:source1).save(flush:true)
                }
            }
        }

    }
    @Transactional
    def addPersonsToSource(def persons, def source) {
        Source source1   = Source.findWhere(name: source)
        if (persons){
            for (person in persons){
                Person person1 = Person.findWhere(name: person )
                PersonSource sourcePerson = PersonSource.findWhere(person: person1, source: source1)
                if (!sourcePerson) {
                    new PersonSource(person: person1, source: source1)
                }
            }
        }
    }
}