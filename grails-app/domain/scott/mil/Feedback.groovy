package scott.mil

class Feedback {
    Topic topic
    String comment
    Date submitDate = new Date()
    Person person
    String response
    boolean reviewed = false

    static constraints = {
        comment maxSize: 3000
        response nullable:true, maxSize: 3000
    }
    String toString() {
        comment
    }
}