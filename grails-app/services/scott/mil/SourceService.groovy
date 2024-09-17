package scott.mil

import grails.core.GrailsApplication
import javax.transaction.Transactional


class SourceService {
    def elasticService

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
    def addPersonsSources(String persons, String sources) {
        def personsArray = persons.split(",")
        def sourcesArray = sources.split(",")
        ArrayList personSourceList = []
            for (person in personsArray) {
                for (source in sourcesArray) {
                    Person person1 = Person.findWhere(name: person)
                    Source source1 = Source.findWhere(name: source)
                    PersonSource personSource = PersonSource.findWhere(person: person1, source: source1)
                    if (!personSource) {
                        def newPersonSource = new PersonSource(person: person1, source: source1)
                        newPersonSource.save(flush:true)
                        Map personSourceMap = [
                            "source": newPersonSource.source.name,
                            "id": newPersonSource.id,
                            "person": newPersonSource.person.name]
                        personSourceList += personSourceMap
                    }
                }
            }
        return personSourceList
    }

    def createElasticIndex (String name) {
        String query = "{\n" +
                "  \"settings\": {\n" +
                "    \"index\": {\n" +
                "      \"number_of_shards\": 1,  \n" +
                "      \"number_of_replicas\": 1 \n" +
                "    }\n" +
                "  },\n" +
                " \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "        \"my_vector\": {\n" +
                "            \"type\": \"dense_vector\",\n" +
                "            \"dims\": 3\n" +
                "        },\n" +
                "        \"my_text\" : {\n" +
                "         \"type\":\"keyword\"\n" +
                "        }\n" +
                "    }\n" +
                " }\n" +
                "}"
        String suffix = "/" + name
        elasticService.putRest(suffix, query)
        }

    def deleteElasticIndex (String name){
        String query = ""
        String suffix = "/" + name
        elasticService.deleteRest(suffix, query)
    }

    def getSources (String person) {
        Person person1 = Person.findWhere(name: person)
        if (person1) {
            def personSourceList = PersonSource.list()
            def sourceList = []
            for (personSource in personSourceList) {
                if (personSource.person == person1){
                    //check source is enabled
                    if (personSource.source.enabled) {
                        sourceList.add(label: personSource.source.name, id: personSource.source.id)
                    }
                }
            }
            def sourceList1 = Source.list()
            for (source in sourceList1) {
                //check source enabled and source is public
                if (source.enabled && source.public1){
                    sourceList.add(label: source.name, id: source.id)
                }
            }
            return sourceList
        }
    }

    def getSource(String person) {
        Person person1 = Person.findWhere(name: person)
        Chat chat = Chat.findWhere(person: person1)
        if (chat.source){
            return ['label':chat.source.name, 'id':chat.source.id]
        }
        else {
            return ['label': 'JLLIS', 'id':1]
        }
    }
}