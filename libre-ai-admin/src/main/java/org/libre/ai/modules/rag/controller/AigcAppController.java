package org.libre.ai.modules.rag.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.entity.AigcAppApi;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.libre.ai.modules.rag.service.AigcAppApiService;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.store.AppStore;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/app")
public class AigcAppController {

	private final AigcAppService aigcAppService;

	private final AigcAppApiService aigcAppApiService;

	private final AppStore appStore;

	private final AigcKnowledgeService knowledgeService;

	@GetMapping("/channel/api/{appId}")
	public R<AigcAppApi> getApiChanel(@PathVariable String appId) {
		List<AigcAppApi> list = aigcAppApiService
			.list(Wrappers.<AigcAppApi>lambdaQuery().eq(AigcAppApi::getAppId, appId));
		return R.ok(list.isEmpty() ? null : list.getFirst());
	}

	@GetMapping("/list")
	public R<List<AigcApp>> list(AigcApp data) {
		return R.ok(aigcAppService.list(data));
	}

	@GetMapping("/page")
	public R<Dict> page(AigcApp data, QueryPage queryPage) {
		return R.ok(MybatisUtil
			.getData(aigcAppService.page(MybatisUtil.wrap(data, queryPage), Wrappers.<AigcApp>lambdaQuery()
				.like(StringUtils.isNotEmpty(data.getName()), AigcApp::getName, data.getName()))));
	}

	@GetMapping("/{id}")
	public R<AigcApp> findById(@PathVariable String id) {
		AigcApp app = aigcAppService.getById(id);
		return R.ok(app);
	}

	@PostMapping
	public R add(@RequestBody AigcApp data) {
		data.setCreateTime(new Date());
		data.setSaveTime(new Date());
		aigcAppService.save(data);
		appStore.init();
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcApp data) {
		// 校验知识库是否是同纬度
		List<String> knowledgeIds = data.getKnowledgeIds();
		if (knowledgeIds != null && !knowledgeIds.isEmpty()) {
			List<AigcKnowledge> list = knowledgeService
				.list(Wrappers.<AigcKnowledge>lambdaQuery().in(AigcKnowledge::getId, knowledgeIds));
			Set<String> modelIds = new HashSet<>();
			Set<String> storeIds = new HashSet<>();
			list.forEach(know -> {
				modelIds.add(know.getEmbedModelId());
				storeIds.add(know.getEmbedStoreId());
			});
			if (modelIds.size() > 1) {
				throw new ServiceException("请选择相同向量库数据源配置的知识库");
			}
			if (storeIds.size() > 1) {
				throw new ServiceException("请选择相同向量模型配置的知识库");
			}
		}

		data.setSaveTime(new Date());
		aigcAppService.updateById(data);
		appStore.init();
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		aigcAppService.removeById(id);
		appStore.init();
		return R.ok();
	}

}