package scott.mil

import grails.converters.JSON
import grails.core.GrailsApplication
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.core5.http.config.Registry
import org.apache.hc.core5.http.config.RegistryBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import javax.transaction.Transactional

@CompileStatic
class SourceService {
    GrailsApplication grailsApplication
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

    def saveSource (Source source) {
        def newSource = new Source(name: source.name, description: source.description, enabled: source.enabled, public1: source.public1)
        newSource.save(flush:true)
        def json = ""
        String query = source.name
        def uri = grailsApplication.config.getProperty('elastic', String.class) + 'collections'
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", '' as ConnectionSocketFactory)
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        HttpPost httpPost = new HttpPost(uri)
        httpPost.addHeader("Content-Type", "application/json")
        StringEntity entity = new StringEntity(query)
        httpPost.setEntity(entity)
        try {
            HttpClientContext clientContext = HttpClientContext.create()
            httpClient.execute(httpPost, clientContext, response -> {
                json = EntityUtils.toString(response.getEntity())
            })
        } catch (e) {
            log.error(e.getMessage())
            log.error("ACAS Asset Vulnerability retrieval error:")
        } finally {
            httpClient.close()
        }
        return newSource
    }

    def getSources (String person) {
        println "Person: " + person
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

    def getSource (String person) {
        Person person1 = Person.findWhere(name: person)
        if (person1) {
            def personSourceList = PersonSource.findAllWhere(person: person1)
            def sourceList = []
            for (personSource in personSourceList) {
                if (personSource.person == person1){
                    //check source is enabled
                    if (personSource.source.enabled) {
                        sourceList.add(label: personSource.source.name, id: personSource.source.id)
                    }
                }
            }
            return sourceList
        }
    }
}