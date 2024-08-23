package scott.mil

import grails.converters.JSON
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@Transactional
class RoleController {
    @ReadOnly
    def index () {
        respond Role.list()
    }

    def show(Role role) {
        respond role
    }

    @Transactional
    def save(Role role) {
        def newRole = new Role (role :role.role, description: role.description)
        newRole.save()
        respond newRole
    }

    @Transactional
    def update(Role role) {
        role.save()
        respond role
    }

    @Transactional
    def delete(Role role) {
        log.warn("Delete user: ${role.role}")
        role.delete (flush:true)
        redirect action:"index", method:"GET"
    }
}