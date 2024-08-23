package scott.mil

import grails.gorm.transactions.ReadOnly
import javax.transaction.Transactional

class FeedbackController {
    @ReadOnly
    def index() {
        respond Feedback.list()
    }

    @ReadOnly
    def show (Feedback feedback) {
        respond feedback
    }
    @Transactional
    def save(Feedback feedback) {
        Feedback feedback1 = new Feedback (topic: feedback.topic, comment: feedback.comment, person: feedback.person, submitDate: new Date())
        feedback1.save(flush:true)
        respond feedback1
    }

    @Transactional
    def update(Feedback feedback) {
        feedback.save(flush:true)
        respond feedback
    }
}