package scott.mil

import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.output.Response
import grails.core.GrailsApplication
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.apache.commons.io.FileUtils
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import io.minio.errors.MinioException

import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.openai.OpenAiTokenizer;


class DocumentService {
    def elasticService
    GrailsApplication grailsApplication

    def documentUpload(def fileName1, String source) {
        File destDir = new File("/documentation/target")
        byte[] buffer = new byte[1024]
        ZipInputStream zis = new ZipInputStream(new FileInputStream("/documentation/" + fileName1))
        ZipEntry zipEntry = zis.getNextEntry()
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        processFiles(source)
        cleanFiles()
    }

    File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    def processFiles(String source) {
        //process files found in /documentation/target directory
       def documents = loadDocumentsRecursively("/documentation/target")
        //define embedding model to be used
        Source source1 = Source.findWhere(name: source)
        List<String> segments

       new File ("/documentation/target").eachFileRecurse () {
           file ->
           {
               //put each file in Minio S3 Bucket
               putMinio(file.absolutePath, file.name, source)
               //convert to text using Tika
               Document document = loadDocument(file.absolutePath, new ApacheTikaDocumentParser())
               String document1 = document.toString()
               new scott.mil.Document (source: source1, title: file.name).save(flush:true)
               segments = splitByTokenSize(document1, 1000,200 )
                   segments.forEach { segment -> {
                       //Create embedding for segment
                       EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
                       Response<Embedding> response = embeddingModel.embed(segment);
                       Embedding embedding = response.content();
                       def embedding1 = embedding.vectorAsList()
                       println "Embedding: " + embedding1
                       //String embeddingString = embedding.toString()
                       //String embeddingString1 = embeddingString.replaceAll("Embedding: Embedding \\{ vector = ", "")
                       //String embeddingString2 = embeddingString1.replace ("\\}","")
                       //println ("Embedding: " + embeddingString2)
                       //upload segment and embedding to specific Elastic Vector Store
                       //put into "source" index
                       //elasticService.postRest(source, segment, embedding)
                   }
               }
           }
       }
    }

    def cleanFiles() {
        //remove files found in /tmp/documentation and remove target directory
        File targetFolder = new File ("/documentation/target")
        FileUtils.cleanDirectory(targetFolder)
        //FileUtils.cleanDirectory(sourceFolder)
    }

    def void putMinio(String absolutePath, String fileName, String source) {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(grailsApplication.config.getProperty("minio", String.class))
                            .credentials(grailsApplication.config.getProperty("minioaccesskey", String.class), grailsApplication.config.getProperty("miniosecretkey", String.class))
                            .build()
            // Check if source(name) bucket exists.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(source).build())
            if (!found) {
                // Make a new bucket using source(name)
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(source).build())
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(source)
                            .object(fileName)
                            .filename(absolutePath)
                            .build());
            log.warn ( "Uploaded ${fileName} to bucket ${source}")
        } catch (MinioException e) {
            log.error ("Error occurred: " + e);
            log.error ("HTTP trace: " + e.httpTrace());
        }
    }

    List<String> splitByTokenSize(String text, int tokenSize, int overlap) {
        // Create a tokenizer instance
        Tokenizer tokenizer = new OpenAiTokenizer();

        // Create a DocumentSplitter with a max segment size of 1024 tokens
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(tokenSize, overlap, tokenizer);

        // Create a Document instance
        Document document = Document.from(text, Metadata.from("document", "0"));

        // Split the text into paragraphs
        List<TextSegment> segments = splitter.split(document);
        return segments
    }
}
