package scott.mil

import grails.gorm.transactions.ReadOnly
import javax.transaction.Transactional


class Type1Controller {
    @ReadOnly
    def index() {
        def types = Type1.list()
        respond types
    }

    @Transactional
    def save(Type1 type1){
        Type1 newType1 = new Type1(type1: type1.type1)
         newType1.save(flush:true)
        respond newType1
    }

    @Transactional
    def update(Type1 type1) {
        type1.save()
        respond type1
    }

    @Transactional
    def delete(Type1 type1) {
        log.warn("Delete type: ${type1.type1}")
        type1.delete (flush:true)
        respond "Complete"
    }
}