package scott.mil

class Chat {
    float accuracy
    Person person
    static belongsTo = [person:Person]

    static constraints = {
    }
}