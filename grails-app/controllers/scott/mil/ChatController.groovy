package scott.mil

import javax.transaction.Transactional

class ChatController {
    def chatService

    @Transactional
    def getChatResponse() {
        def answer = chatService.getAnswer(params.message, params.person)
        def data = ["data": answer]
        respond data
    }
}

