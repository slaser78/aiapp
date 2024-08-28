package scott.mil

class Chat {
    float accuracy = 0.7
    Type1 type1
    Person person
    static belongsTo = [person:Person]

    static constraints = {
        type1 nullable: true
    }
}