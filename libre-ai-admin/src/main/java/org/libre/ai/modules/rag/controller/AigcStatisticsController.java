package org.libre.ai.modules.rag.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.libre.ai.modules.rag.mapper.AigcAppMapper;
import org.libre.ai.modules.rag.mapper.AigcKnowledgeMapper;
import org.libre.ai.modules.rag.mapper.AigcMessageMapper;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tycoding
 * @since 2024/6/8
 */
@RequestMapping("/api/aigc/statistic")
@RestController
@AllArgsConstructor
public class AigcStatisticsController {

	private final AigcMessageMapper aigcMessageMapper;

	// private final SysUserMapper userMapper;
	private final AigcKnowledgeMapper aigcKnowledgeMapper;

	private final AigcAppMapper aigcAppMapper;

	// @GetMapping("/requestBy30")
	// public R request30Chart() {
	// return R.ok(aigcMessageMapper.getReqChartBy30());
	// }

	@GetMapping("/tokenBy30")
	public R token30Chart() {
		return R.ok(aigcMessageMapper.getTokenChartBy30());
	}

	@GetMapping("/token")
	public R tokenChart() {
		return R.ok(aigcMessageMapper.getTokenChart());
	}

	@GetMapping("/request")
	public R requestChart() {
		return R.ok(aigcMessageMapper.getReqChart());
	}

	@GetMapping("/home")
	public R home() {
		Dict reqData = aigcMessageMapper.getCount();
		Dict totalData = aigcMessageMapper.getTotalSum();
		// Dict userData = userMapper.getCount();
		Long totalKnowledge = aigcKnowledgeMapper.selectCount(Wrappers.query());
		Long totalPrompt = aigcAppMapper.selectCount(Wrappers.query());
		Dict result = Dict.create();
		result.putAll(reqData);
		result.putAll(totalData);
		// result.putAll(userData);
		result.set("totalKnowledge", totalKnowledge.intValue()).set("totalPrompt", totalPrompt.intValue());
		return R.ok(result);
	}

}
