package scott.mil

class Chat {
    float accuracy
    Person person
    static belongsTo = [person:Person]
    Source source

    static constraints = {
        source nullable: true
    }
}