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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.mapper.AigcDocsMapper;
import org.libre.ai.modules.rag.service.EmbeddingService;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/docs")
public class AigcDocsController {

	private final AigcDocsMapper docsMapper;

	private final EmbeddingService embeddingService;

	@GetMapping("/list")
	public R<List<AigcDocs>> list(AigcDocs data) {
		return R.ok(docsMapper.selectList(Wrappers.<AigcDocs>lambdaQuery()
			.eq(StringUtils.isNotBlank(data.getKnowledgeId()), AigcDocs::getKnowledgeId, data.getKnowledgeId())
			.orderByDesc(AigcDocs::getCreateTime)));
	}

	@GetMapping("/page")
	public R list(AigcDocs data, QueryPage queryPage) {
		Page<AigcDocs> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
		return R.ok(MybatisUtil.getData(docsMapper.selectPage(page,
				Wrappers.<AigcDocs>lambdaQuery()
					.eq(data.getKnowledgeId() != null, AigcDocs::getKnowledgeId, data.getKnowledgeId())
					.eq(data.getSliceStatus() != null, AigcDocs::getSliceStatus, data.getSliceStatus())
					.orderByDesc(AigcDocs::getCreateTime))));
	}

	@GetMapping("/{id}")
	public R<AigcDocs> findById(@PathVariable String id) {
		return R.ok(docsMapper.selectById(id));
	}

	@PostMapping
	public R add(@RequestBody AigcDocs data) {
		docsMapper.insert(data);
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcDocs data) {
		docsMapper.updateById(data);
		return R.ok();
	}

	@DeleteMapping("/{id}")
	@Transactional
	public R delete(@PathVariable String id) {
		// 删除切面数据
		embeddingService.clearDocSlices(id);

		// 删除文档
		docsMapper.deleteById(id);
		return R.ok();
	}

}
