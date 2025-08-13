package org.libre.ai.modules.rag.core.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.component.EmbeddingRefreshEvent;
import org.libre.ai.component.ProviderRefreshEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author tycoding
 * @since 2024/6/16
 */
@Slf4j
@Component
@AllArgsConstructor
public class ProviderListener {

	private final ModelStoreFactory providerInitialize;

	private final EmbeddingStoreFactory embeddingStoreInitialize;

	@EventListener
	public void providerEvent(ProviderRefreshEvent event) {
		log.info("refresh provider beans begin......");
		providerInitialize.init();
		log.info("refresh provider beans success......");
	}

	@EventListener
	public void providerEvent(EmbeddingRefreshEvent event) {
		log.info("refresh embedding beans begin......");
		embeddingStoreInitialize.init();
		log.info("refresh embedding beans success......");
	}

}
