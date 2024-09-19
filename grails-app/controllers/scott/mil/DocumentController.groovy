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
        respond "Complete"
    }

    def documentDownload() {
        //retrieve source and fileName
        String source = params.getProperty("source")
        String fileName = params.getProperty("fileName")
        //retrieve Minio "share" URL
    }
}