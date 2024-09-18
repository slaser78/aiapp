package scott.mil

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.model.openai.OpenAiTokenizer
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import grails.core.GrailsApplication
import groovy.transform.CompileStatic
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.apache.commons.io.FileUtils

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import io.minio.errors.MinioException

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.model.Tokenizer;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*
import static dev.langchain4j.data.document.splitter.DocumentSplitters.*;

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
        //Create embedding store
        EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl(grailsApplication.config.getProperty("elastic",String.class))
                .dimension(384)
                .build()

        //define embedding model to be used
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel()
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            //.documentTransformer(...)
            .documentSplitter(recursive(1000, 200, new OpenAiTokenizer()))
            //.textSegmentTransformer(...)
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()
        //put each file in Minio S3 bucket
       new File ("/documentation/target").eachFileRecurse () {
           file ->
               {
                   //put each file in Minio S3 Bucket
                   putMinio(file.absolutePath, file.name, source)
                   //convert to text using Tika
                   dev.langchain4j.data.document.Document document = loadDocument(file.absolutePath, new ApacheTikaDocumentParser())
                   ingestor.ingest(document)
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
}
