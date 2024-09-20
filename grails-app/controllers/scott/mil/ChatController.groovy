package scott.mil

import grails.gorm.transactions.ReadOnly
import javax.transaction.Transactional

class ChatController {
    def chatService

    @Transactional
    def getChatResponse() {
        def answer = chatService.getAnswer(params.message, params.person)
        def data = ["data": answer]
        respond data
    }

    @ReadOnly
    def getChatSettings() {
        def settings = chatService.getChatSettings(params.person)
        respond settings
    }

    @ReadOnly
    def getChat(){
        Person person = Person.findWhere(name: params.person)
        Chat chat = Chat.findWhere(person: person)
        def chatValue
        if (chat.source) {
            chatValue = ["id":chat.id, "personId":chat.person.id, "person": chat.person.name, "source": chat.source.name, "sourceId": chat.source.id ]
        }
        else {
            def sources = Source.findAllWhere(enabled: true, public1: true)
            chatValue = ["id":chat.id, "personId":chat.person.id, "person": chat.person.name, "source": sources[0].name, "sourceId": sources[0].id ]
        }
        respond chatValue
    }

    @Transactional
    def setChatSettings() {
        String temperature = params.temperature
        String person = params.person
        String source = params.source
        if (params.id) {
            String id = params.id
            chatService.setChatSettings(temperature, person, source, id)
        } else {
            chatService.setChatSettings(temperature, person, source, null)
        }
        respond "Complete"
    }
}

