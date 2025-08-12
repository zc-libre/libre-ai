package org.libre.ai.modules.rag.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.libre.ai.component.EmbeddingRefreshEvent;
import org.libre.ai.modules.rag.entity.AigcEmbedStore;
import org.libre.ai.modules.rag.service.AigcEmbedStoreService;
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
@RequestMapping("/api/aigc/embed-store")
public class AigcEmbedStoreController {

	private final AigcEmbedStoreService embedStoreService;

	private final SpringContextHolder contextHolder;

	@GetMapping("/list")
	public R<List<AigcEmbedStore>> list(AigcEmbedStore data) {
		List<AigcEmbedStore> list = embedStoreService.list(Wrappers.lambdaQuery());
		list.forEach(this::hide);
		return R.ok(list);
	}

	@GetMapping("/page")
	public R<Dict> page(AigcEmbedStore embedStore, QueryPage queryPage) {
		IPage<AigcEmbedStore> page = embedStoreService.page(MybatisUtil.wrap(embedStore, queryPage),
				Wrappers.<AigcEmbedStore>lambdaQuery()
					.like(StrUtil.isNotBlank(embedStore.getName()), AigcEmbedStore::getName, embedStore.getName())
					.eq(StrUtil.isNotBlank(embedStore.getProvider()), AigcEmbedStore::getProvider,
							embedStore.getProvider())
					.orderByDesc(AigcEmbedStore::getCreateTime));
		page.getRecords().forEach(this::hide);
		return R.ok(MybatisUtil.getData(page));
	}

	@GetMapping("/{id}")
	public R<AigcEmbedStore> findById(@PathVariable String id) {
		AigcEmbedStore store = embedStoreService.getById(id);
		hide(store);
		return R.ok(store);
	}

	@PostMapping
	public R<AigcEmbedStore> add(@RequestBody AigcEmbedStore data) {
		if (StrUtil.isNotBlank(data.getPassword()) && data.getPassword().contains("*")) {
			data.setPassword(null);
		}
		embedStoreService.save(data);
		SpringContextHolder.publishEvent(new EmbeddingRefreshEvent(data));
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcEmbedStore data) {
		if (StrUtil.isNotBlank(data.getPassword()) && data.getPassword().contains("*")) {
			data.setPassword(null);
		}
		embedStoreService.updateById(data);
		SpringContextHolder.publishEvent(new EmbeddingRefreshEvent(data));
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		AigcEmbedStore store = embedStoreService.getById(id);
		if (store != null) {
			embedStoreService.removeById(id);
			contextHolder.unregisterBean(id);
		}
		return R.ok();
	}

	private void hide(AigcEmbedStore data) {
		if (data == null || StrUtil.isBlank(data.getPassword())) {
			return;
		}
		String key = StrUtil.hide(data.getPassword(), 0, data.getPassword().length());
		data.setPassword(key);
	}

}
