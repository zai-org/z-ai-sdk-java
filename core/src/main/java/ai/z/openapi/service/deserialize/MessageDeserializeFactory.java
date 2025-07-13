package ai.z.openapi.service.deserialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ai.z.openapi.service.assistant.AssistantChoice;
import ai.z.openapi.service.assistant.CompletionUsage;
import ai.z.openapi.service.assistant.ErrorInfo;
import ai.z.openapi.service.deserialize.assistant.AssistantChoiceDeserializer;
import ai.z.openapi.service.deserialize.assistant.CompletionUsageDeserializer;
import ai.z.openapi.service.deserialize.assistant.ErrorInfoDeserializer;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeInfoDeserializer;
import ai.z.openapi.service.deserialize.knowledge.KnowledgePageDeserializer;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeStatisticsDeserializer;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeUsedDeserializer;
import ai.z.openapi.service.deserialize.videos.VideoObjectDeserializer;
import ai.z.openapi.service.deserialize.videos.VideoResultDeserializer;
import ai.z.openapi.service.knowledge.KnowledgeInfo;
import ai.z.openapi.service.knowledge.KnowledgePage;
import ai.z.openapi.service.knowledge.KnowledgeStatistics;
import ai.z.openapi.service.knowledge.KnowledgeUsed;
import ai.z.openapi.service.videos.VideoObject;
import ai.z.openapi.service.videos.VideoResult;

public class MessageDeserializeFactory {

	public static ObjectMapper defaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		SimpleModule module = new SimpleModule();

		module.addDeserializer(VideoResult.class, new VideoResultDeserializer());
		module.addDeserializer(VideoObject.class, new VideoObjectDeserializer());
		module.addDeserializer(KnowledgeInfo.class, new KnowledgeInfoDeserializer());
		module.addDeserializer(KnowledgeUsed.class, new KnowledgeUsedDeserializer());
		module.addDeserializer(KnowledgeStatistics.class, new KnowledgeStatisticsDeserializer());
		module.addDeserializer(KnowledgePage.class, new KnowledgePageDeserializer());
		module.addDeserializer(KnowledgeInfo.class, new KnowledgeInfoDeserializer());
		module.addDeserializer(AssistantChoice.class, new AssistantChoiceDeserializer());
		module.addDeserializer(CompletionUsage.class, new CompletionUsageDeserializer());
		module.addDeserializer(ErrorInfo.class, new ErrorInfoDeserializer());
		mapper.registerModule(module);

		return mapper;
	}

}
