package scott.mil

class Source {
    String name
    String collection
    String description
    static hasMany = [document:Document]
    boolean enabled = false
    boolean public1 = false

    static constraints = {
        name unique: true
        description nullable: true
        collection nullable: true
    }

    String toString() {
        name
    }
}