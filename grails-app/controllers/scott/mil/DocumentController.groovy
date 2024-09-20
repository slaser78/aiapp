package scott.mil

class DocumentController {
    def documentService

    def index() {
        respond Document.list()
    }

    def documentUpload(){
        def file = request.getFile('file')
        String fileName1 = (request.getParameter("fileName")).toString()
        String source1 = (request.getParameter("source")).toString()
        file.transferTo(new File('/documentation/' + fileName1))
        documentService.documentUpload(fileName1, source1)
        respond "Complete"
    }

    def delete(Document document) {
        document.delete(flush:true)
        //delete document entries from Minio and Elastic Search
        respond "Complete"
    }

    //used to download document from Minio
    //requires generation of temporary Minio share URL
    def documentDownload() {
        //retrieve source and fileName
        String source = params.getProperty("source")
        String fileName = params.getProperty("fileName")
        //retrieve Minio "share" URL
    }

    //returns public and documents associated with person's sources
    def getDocuments() {
        Person person = Person.findWhere(name: params.person)
        def personSources = PersonSource.findAllWhere(person: person)
        def sources = Source.findAllWhere(public1: true, enabled: true)
        for (personSource in personSources) {
            if (!sources.contains(personSource.source)) {
                sources.add(personSource.source)
            }
        }
        def documents = []
        for (source in sources) {
            def documents1 = Document.findAllWhere(source: source)
            if (documents1) {
                for (document in documents1) {
                    documents.add(document)
                }
            }
            respond documents
        }
    }
}