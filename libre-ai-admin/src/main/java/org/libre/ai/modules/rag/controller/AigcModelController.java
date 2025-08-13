package org.libre.ai.modules.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.libre.ai.component.ProviderRefreshEvent;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.service.AigcModelService;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.libre.ai.modules.rag.utils.SpringContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/model")
public class AigcModelController {

	private final AigcModelService modelService;

	private final SpringContextHolder contextHolder;

	@GetMapping("/list")
	public R<List<AigcModel>> list(AigcModel data) {
		return R.ok(modelService.list(data));
	}

	@GetMapping("/page")
	public R list(AigcModel data, QueryPage queryPage) {
		Page<AigcModel> iPage = modelService.page(data, queryPage);
		return R.ok(MybatisUtil.getData(iPage));
	}

	@GetMapping("/{id}")
	public R<AigcModel> findById(@PathVariable String id) {
		return R.ok(modelService.selectById(id));
	}

	@PostMapping
	public R add(@RequestBody AigcModel data) {
		if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
			data.setApiKey(null);
		}
		if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
			data.setSecretKey(null);
		}
		modelService.save(data);
		SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcModel data) {
		if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
			data.setApiKey(null);
		}
		if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
			data.setSecretKey(null);
		}
		modelService.updateById(data);
		SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		modelService.removeById(id);

		// Delete dynamically registered beans, according to ID
		contextHolder.unregisterBean(id);
		return R.ok();
	}

}
