package scott.mil

class Role {
    String role
    String description

    static constraints = {
        role unique:true
    }
    String toString() {
        role
    }
}