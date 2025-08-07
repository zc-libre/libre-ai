package org.libre.ai.modules.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.mapper.AigcAppMapper;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.service.AigcModelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tycoding
 * @since 2024/7/26
 */
@RequiredArgsConstructor
@Service
public class AigcAppServiceImpl extends ServiceImpl<AigcAppMapper, AigcApp> implements AigcAppService {

	private final AigcModelService aigcModelService;

	private final AigcKnowledgeService aigcKnowledgeService;

	@Override
	public List<AigcApp> list(AigcApp data) {
		List<AigcApp> list = baseMapper.selectList(Wrappers.<AigcApp>lambdaQuery()
			.like(StrUtil.isNotBlank(data.getName()), AigcApp::getName, data.getName()));

		Map<String, List<AigcModel>> modelMap = aigcModelService.list(new AigcModel())
			.stream()
			.collect(Collectors.groupingBy(AigcModel::getId));
		Map<String, List<AigcKnowledge>> knowledgeMap = aigcKnowledgeService.list()
			.stream()
			.collect(Collectors.groupingBy(AigcKnowledge::getId));
		list.forEach(i -> {
			List<AigcModel> models = modelMap.get(i.getModelId());
			if (models != null) {
				i.setModel(models.get(0));
			}
			if (i.getKnowledgeIds() != null) {
				List<AigcKnowledge> knowledges = new ArrayList<>();
				i.getKnowledgeIds().forEach(k -> {
					List<AigcKnowledge> items = knowledgeMap.get(k);
					if (items != null) {
						knowledges.add(items.get(0));
					}
				});
				i.setKnowledges(knowledges);
			}
		});
		return list;
	}

	@Override
	public AigcApp getById(String id) {
		AigcApp app = baseMapper.selectById(id);
		if (app != null) {
			String modelId = app.getModelId();
			if (modelId != null) {
				app.setModel(aigcModelService.selectById(modelId));
			}
			List<String> knowledgeIds = app.getKnowledgeIds();
			if (knowledgeIds != null && !knowledgeIds.isEmpty()) {
				app.setKnowledges(aigcKnowledgeService
					.list(Wrappers.<AigcKnowledge>lambdaQuery().in(AigcKnowledge::getId, knowledgeIds)));
			}
		}
		return app;
	}

}
