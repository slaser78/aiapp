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
        if (chat) {
            def chatValue = ["id":chat.id, "personId":chat.person.id, "person": chat.person.name, "source": chat.source.name, "sourceId": chat.source.id ]
            respond chatValue
        }
        else {
            return null
        }
    }

    @Transactional
    def setChatSettings() {
        String accuracy = params.accuracy
        String person = params.person
        String source = params.source
        if (params.id) {
            String id = params.id
            chatService.setChatSettings(accuracy, person, source, id)
        } else {
            chatService.setChatSettings(accuracy, person, source, null)
        }
        respond "Complete"
    }
}

