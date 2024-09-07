package scott.mil

class Chat {
    float accuracy = 0.7
    Person person
    static belongsTo = [person:Person]
    Source source

    static constraints = {
        source nullable: true
    }
}