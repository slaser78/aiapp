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

    @Transactional
    def setChatSettings() {
        Person person = Person.findWhere(name: params.person)
        Float accuracy = params.accuracy.toFloat()
        Type1 type1 = Type1.findWhere(type1: params.type1)
        respond (chatService.setChatSettings(person, accuracy, type1))
    }
}

