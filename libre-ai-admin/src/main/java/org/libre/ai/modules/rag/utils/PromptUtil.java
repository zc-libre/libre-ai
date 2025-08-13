package org.libre.ai.modules.rag.utils;

import cn.hutool.core.bean.BeanUtil;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.libre.ai.modules.rag.dto.PromptConstant;

import java.util.Map;

/**
 * @author tycoding
 * @since 2024/3/1
 */
public class PromptUtil {

	public static Prompt build(String message) {
		return new Prompt(message);
	}

	public static Prompt build(String message, String promptText) {
		return new PromptTemplate(promptText + PromptConstant.EMPTY).apply(Map.of(PromptConstant.QUESTION, message));
	}

	public static Prompt build(String message, String promptText, Object param) {
		Map<String, Object> params = BeanUtil.beanToMap(param, false, true);
		params.put(PromptConstant.QUESTION, message);
		return new PromptTemplate(promptText).apply(params);
	}

	public static Prompt buildDocs(String message) {
		return new PromptTemplate(PromptConstant.DOCUMENT).apply(Map.of(PromptConstant.QUESTION, message));
	}

}
