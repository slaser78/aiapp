package scott.mil

class Person {
    String name
    Role role
    Date date = new Date()

    static constraints = {
    }

    String toString() {
        name
    }
}