package scott.mil

class Source {
    String name
    String description
    static hasMany = [document:Document]
    boolean enabled = false
    boolean public1 = false

    static constraints = {
        name unique: true
        description nullable: true
    }

    String toString() {
        name
    }
}