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
    def save (Chat chat) {
        chat.save()
        respond chat
    }

    @Transactional
    def edit (Chat chat) {
        chat.save()
        respond chat
    }

    @ReadOnly
    def show (Chat chat){
        respond chat
    }

    def getChat(){
        Person person = Person.findWhere(name: params.person)
        Chat chat = Chat.findWhere(person: person)
        if (chat) {
            return chat
        }
        else {
            return null
        }
    }
}

