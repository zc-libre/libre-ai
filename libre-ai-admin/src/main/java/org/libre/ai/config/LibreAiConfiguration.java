package org.libre.ai.config;

import org.libre.ai.modules.rag.properties.ChatProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ChatProperties.class)
public class LibreAiConfiguration {

}
