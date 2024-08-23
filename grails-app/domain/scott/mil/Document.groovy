package scott.mil

class Document {
    String title
    Source source
    static belongsTo = [source:Source]
    static constraints = {

    }
    String toString() {
        title
    }
}