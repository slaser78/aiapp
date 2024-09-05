package scott.mil

class Chat {
    float accuracy
    Person person
    static belongsTo = [person:Person]
    static hasMany = [source:Source]

    static constraints = {
        source nullable: true
    }
}