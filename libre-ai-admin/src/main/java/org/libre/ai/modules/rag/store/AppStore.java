package org.libre.ai.modules.rag.store;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tycoding
 * @since 2024/8/8
 */
@Slf4j
@Component
@AllArgsConstructor
public class AppStore {

	private static final Map<String, AigcApp> appMap = new HashMap<>();

	private final AigcAppService aigcAppService;

	@PostConstruct
	public void init() {
		log.info("initialize app config list...");
		List<AigcApp> list = aigcAppService.list();
		list.forEach(i -> appMap.put(i.getId(), i));
	}

	public AigcApp get(String appId) {
		return appMap.get(appId);
	}

}
