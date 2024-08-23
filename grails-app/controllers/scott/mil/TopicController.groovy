package scott.mil


class TopicController {

    def index() {
        def topics = Topic.list()
        respond topics
    }
}