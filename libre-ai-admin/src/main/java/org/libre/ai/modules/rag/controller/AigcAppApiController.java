package org.libre.ai.modules.rag.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.libre.ai.modules.rag.entity.AigcAppApi;
import org.libre.ai.modules.rag.service.AigcAppApiService;
import org.libre.ai.modules.rag.store.AppChannelStore;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/app/api")
public class AigcAppApiController {

	private final AigcAppApiService appApiService;

	private final AppChannelStore appChannelStore;

	@GetMapping("/create/{id}/{channel}")
	public R create(@PathVariable String id, @PathVariable String channel) {
		String uuid = "libre" + IdUtil.simpleUUID();
		appApiService.save(new AigcAppApi().setAppId(id).setApiKey(uuid).setChannel(channel).setCreateTime(new Date()));
		appChannelStore.init();
		return R.ok();
	}

	@GetMapping("/list")
	public R<List<AigcAppApi>> list(AigcAppApi data) {
		List<AigcAppApi> list = appApiService.list(Wrappers.<AigcAppApi>lambdaQuery()
			.eq(StrUtil.isNotBlank(data.getAppId()), AigcAppApi::getAppId, data.getAppId())
			.eq(StrUtil.isNotBlank(data.getChannel()), AigcAppApi::getChannel, data.getChannel())
			.orderByDesc(AigcAppApi::getCreateTime));
		return R.ok(list);
	}

	@GetMapping("/page")
	public R<Dict> page(AigcAppApi data, QueryPage queryPage) {
		IPage<AigcAppApi> iPage = appApiService.page(MybatisUtil.wrap(data, queryPage),
				Wrappers.<AigcAppApi>lambdaQuery()
					.like(StringUtils.isNotEmpty(data.getAppId()), AigcAppApi::getAppId, data.getAppId())
					.eq(StrUtil.isNotBlank(data.getChannel()), AigcAppApi::getChannel, data.getChannel())
					.orderByDesc(AigcAppApi::getCreateTime));
		return R.ok(MybatisUtil.getData(iPage));
	}

	@GetMapping("/{id}")
	public R<AigcAppApi> findById(@PathVariable String id) {
		AigcAppApi api = appApiService.getById(id);
		return R.ok(api);
	}

	@PostMapping
	public R add(@RequestBody AigcAppApi data) {
		data.setCreateTime(new Date());
		appApiService.save(data);
		appChannelStore.init();
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcAppApi data) {
		appApiService.updateById(data);
		appChannelStore.init();
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		appApiService.removeById(id);
		appChannelStore.init();
		return R.ok();
	}

}