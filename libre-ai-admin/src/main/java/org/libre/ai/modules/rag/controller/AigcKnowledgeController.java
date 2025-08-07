/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libre.ai.modules.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.core.provider.EmbeddingProvider;
import org.libre.ai.modules.rag.core.provider.KnowledgeStoreFactory;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.entity.AigcEmbedStore;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.mapper.AigcDocsMapper;
import org.libre.ai.modules.rag.service.AigcEmbedStoreService;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.service.AigcModelService;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/knowledge")
public class AigcKnowledgeController {

	private final AigcKnowledgeService kbService;

	private final AigcDocsMapper docsMapper;

	private final AigcEmbedStoreService embedStoreService;

	private final AigcModelService modelService;

	private final EmbeddingProvider embeddingProvider;

	private final KnowledgeStoreFactory knowledgeStore;

	@GetMapping("/list")
	public R<List<AigcKnowledge>> list(AigcKnowledge data) {
		List<AigcKnowledge> list = kbService
			.list(Wrappers.<AigcKnowledge>lambdaQuery().orderByDesc(AigcKnowledge::getCreateTime));
		build(list);
		return R.ok(list);
	}

	private void build(List<AigcKnowledge> records) {
		Map<String, List<AigcEmbedStore>> embedStoreMap = embedStoreService.list()
			.stream()
			.collect(Collectors.groupingBy(AigcEmbedStore::getId));
		Map<String, List<AigcModel>> embedModelMap = modelService.list()
			.stream()
			.collect(Collectors.groupingBy(AigcModel::getId));
		Map<String, List<AigcDocs>> docsMap = docsMapper.selectList(Wrappers.lambdaQuery())
			.stream()
			.collect(Collectors.groupingBy(AigcDocs::getKnowledgeId));
		records.forEach(item -> {
			List<AigcDocs> docs = docsMap.get(item.getId());
			if (docs != null) {
				item.setDocsNum(docs.size());
				item.setTotalSize(docs.stream().filter(d -> d.getSize() != null).mapToLong(AigcDocs::getSize).sum());
			}
			if (item.getEmbedModelId() != null) {
				List<AigcModel> list = embedModelMap.get(item.getEmbedModelId());
				item.setEmbedModel(list == null ? null : list.get(0));
			}
			if (item.getEmbedStoreId() != null) {
				List<AigcEmbedStore> list = embedStoreMap.get(item.getEmbedStoreId());
				item.setEmbedStore(list == null ? null : list.get(0));
			}
		});
	}

	@GetMapping("/page")
	public R list(AigcKnowledge data, QueryPage queryPage) {
		Page<AigcKnowledge> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
		LambdaQueryWrapper<AigcKnowledge> queryWrapper = Wrappers.<AigcKnowledge>lambdaQuery()
			.like(!StrUtil.isBlank(data.getName()), AigcKnowledge::getName, data.getName())
			.orderByDesc(AigcKnowledge::getCreateTime);
		Page<AigcKnowledge> iPage = kbService.page(page, queryWrapper);

		build(iPage.getRecords());

		return R.ok(MybatisUtil.getData(iPage));
	}

	@GetMapping("/{id}")
	public R<AigcKnowledge> findById(@PathVariable String id) {
		AigcKnowledge knowledge = kbService.getById(id);
		if (knowledge.getEmbedStoreId() != null) {
			knowledge.setEmbedStore(embedStoreService.getById(knowledge.getEmbedStoreId()));
		}
		if (knowledge.getEmbedModelId() != null) {
			knowledge.setEmbedModel(modelService.getById(knowledge.getEmbedModelId()));
		}
		return R.ok(knowledge);
	}

	@PostMapping
	public R add(@RequestBody AigcKnowledge data) {
		data.setCreateTime(String.valueOf(System.currentTimeMillis()));
		kbService.save(data);
		knowledgeStore.init();
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcKnowledge data) {
		kbService.updateById(data);
		knowledgeStore.init();
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		kbService.removeKnowledge(id);
		knowledgeStore.init();
		return R.ok();
	}

}
