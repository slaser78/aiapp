package scott.mil

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.UserMessage
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import grails.converters.JSON
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.mapdb.DB;
import org.mapdb.DBMaker

import javax.servlet.http.HttpServletRequest;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.INTEGER;
import static org.mapdb.Serializer.STRING;

interface Assistant {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage)
}

@CompileStatic
class ChatService {

    PersistentChatMemoryStore store = new PersistentChatMemoryStore()

    def getAnswer(String message, String person) {
        Person person1 = Person.findWhere(name: person)
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>()

        ChatLanguageModel model = OpenAiChatModel.builder()
            .responseFormat("Response<AiMessage>")
            .apiKey("demo")
            .build()
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(person1.id.toInteger())
                .maxMessages(10)
                .chatMemoryStore(store)
                .build()

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever (EmbeddingStoreContentRetriever.from(embeddingStore))
                .build()
        return (assistant.chat(person1.id.toInteger(), message))
    }

    def getChatSettings(String person){
        Person person1 = Person.findWhere(name: person)
        Chat chat = Chat.findWhere(person: person1)
        def accuracy
        if (chat) {
            accuracy = [accuracy: chat.accuracy]
        } else {
            accuracy = [accuracy: 0.7]
        }
        return accuracy
    }

    def setChatSettings (Person person, Float accuracy) {
        Chat chat = Chat.findWhere(person: person)
        if (chat){
            chat.accuracy = accuracy
            chat.save(flush:true)
            return chat.accuracy
        } else {
            Chat chat1 = new Chat(accuracy: accuracy, person: person)
            chat1.save(flush:true)
            return chat1.accuracy
        }
    }
}

class PersistentChatMemoryStore implements ChatMemoryStore {

    DB db = DBMaker.fileDB("multi-user-chat-memory.db").transactionEnable().make()
    Map<Integer, String> map = db.hashMap("messages", INTEGER, STRING).createOrOpen()

    @Override
    List<ChatMessage> getMessages(Object memoryId) {
        String json = map.get((int) memoryId);
        return messagesFromJson(json)
    }

    @Override
    void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = messagesToJson(messages);
        map.put((int) memoryId, json);
        db.commit();
    }

    @Override
    void deleteMessages(Object memoryId) {
        map.remove((int) memoryId);
        db.commit();
    }
}