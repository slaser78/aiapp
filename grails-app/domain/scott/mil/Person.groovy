package scott.mil

class Person {
    String name
    Role role
    Date date = new Date()
    Chat chat

    static constraints = {
    }

    String toString() {
        name
    }
}