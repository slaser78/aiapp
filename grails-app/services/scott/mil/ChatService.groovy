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
import groovy.transform.CompileStatic
import org.mapdb.DB;
import org.mapdb.DBMaker;
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
        def settings = []
        if (person1.chat) {
        Map accuracyMap =    ["accuracy": person1.chat.accuracy]
        Map typeMap =    ["type": person1.chat.type1.type1]
            settings = (accuracyMap + (typeMap as Map<String, Float>))
        }
        else {
            Map accuracyMap = ["accuracy": 0.7]
            Map typeMap = ["type": "Chat"]
            settings = (accuracyMap + (typeMap as Map<String, BigDecimal>))
        }
        println settings
        return settings
    }

    def setChatSettings (Float accuracy, String type1) {
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