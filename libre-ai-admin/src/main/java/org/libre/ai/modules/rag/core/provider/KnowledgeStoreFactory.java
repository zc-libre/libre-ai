package org.libre.ai.modules.rag.core.provider;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcEmbedStore;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.service.AigcEmbedStoreService;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.service.AigcModelService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author tycoding
 * @since 2024/10/29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeStoreFactory {

	private final AigcKnowledgeService knowledgeService;

	private final AigcModelService modelService;

	private final AigcEmbedStoreService embedStoreService;

	private final Map<String, AigcKnowledge> knowledgeMap = new ConcurrentHashMap<>();

	@Async
	@PostConstruct
	public void init() {
		knowledgeMap.clear();
		List<AigcKnowledge> list = knowledgeService.list();
		Map<String, List<AigcModel>> modelMap = modelService.list()
			.stream()
			.collect(Collectors.groupingBy(AigcModel::getId));
		Map<String, List<AigcEmbedStore>> storeMap = embedStoreService.list()
			.stream()
			.collect(Collectors.groupingBy(AigcEmbedStore::getId));
		list.forEach(know -> {
			if (know.getEmbedModelId() != null) {
				List<AigcModel> models = modelMap.get(know.getEmbedModelId());
				know.setEmbedModel(models == null ? null : models.get(0));
			}
			if (know.getEmbedStoreId() != null) {
				List<AigcEmbedStore> stores = storeMap.get(know.getEmbedStoreId());
				know.setEmbedStore(stores == null ? null : stores.get(0));
			}
			knowledgeMap.put(know.getId(), know);
		});
	}

	public AigcKnowledge getKnowledge(String knowledgeId) {
		return knowledgeMap.get(knowledgeId);
	}

	public boolean containsKnowledge(String knowledgeId) {
		return knowledgeMap.containsKey(knowledgeId);
	}

}
