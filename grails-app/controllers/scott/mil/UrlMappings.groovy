package scott.mil

class UrlMappings {
    static mappings = {
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")
        "/"(controller: 'application', action:'index')
        "500"(view:'/error')
        "404"(view:'/notFound')
        "/usernameGetInitial" (controller: "person", action: "usernameGetInitial")
        "/getChatResponse" (controller: "chat", action:"getChatResponse")
        "/documentUpload" (controller: "document", action: "documentUpload")
        "/getPersonSource" (controller: "personSource", action: "getPersonSource")
        "/setPerson" (controller: "person", action: 'setPerson')
        "/setSource" (controller: "personSource", action: 'setSource')
        "/addPersonsSources" (controller: 'personSource', action: 'addPersonsSources')
        "/getChatSettings" (controller: 'chat', action: 'getChatSettings')
        "/setChatSettings" (controller: 'chat', action: 'setChatSettings')
        "/getTypes"(controller: 'type1', action: 'getTypes')
        "/getSources" (controller: "source", action: 'getSources')
        "/getSource" (controller: "source", action: 'getSource')
        "/getChat" (controller: "chat", action: 'getChat')
    }
}
