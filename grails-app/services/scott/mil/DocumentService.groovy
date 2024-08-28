package scott.mil

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import grails.core.GrailsApplication
import groovy.transform.CompileStatic
import org.testcontainers.elasticsearch.ElasticsearchContainer
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.apache.commons.io.FileUtils

@CompileStatic
class DocumentService {
    GrailsApplication grailsApplication
    def documentUpload(def source1, def fileName1) {
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
        processFiles()
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

    def processFiles() {
        //process files found in /documentation/target directory
       def documents = FileSystemDocumentLoader.loadDocumentsRecursively("/documentation/target")
        try {
            EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
            .serverUrl(grailsApplication.config.getProperty("elastic",String.class))
            .dimension(384)
            .build()
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel()
            //chunk document
            String chunk = ""  //insert chunk to be processed for embedding
            TextSegment segment = TextSegment.from(chunk)
            Embedding embedding = embeddingModel.embed(segment).content()
            embeddingStore.add(embedding, segment)
        } catch (e) {
            println e.getMessage()
        }
    }

    def cleanFiles() {
        //remove files found in /tmp/documentation and remove target directory
        File targetFolder = new File ("/documentation/target")
        FileUtils.cleanDirectory(targetFolder)
        //FileUtils.cleanDirectory(sourceFolder)
    }
}
