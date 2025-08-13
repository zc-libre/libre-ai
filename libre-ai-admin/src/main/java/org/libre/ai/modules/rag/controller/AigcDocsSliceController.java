package org.libre.ai.modules.rag.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcDocsSlice;
import org.libre.ai.modules.rag.mapper.AigcDocsSliceMapper;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aigc/docs/slice")
public class AigcDocsSliceController {

	private final AigcDocsSliceMapper docsSliceMapper;

	@GetMapping("/list")
	public R<List<AigcDocsSlice>> list(AigcDocsSlice data) {
		return R.ok(docsSliceMapper
			.selectList(Wrappers.<AigcDocsSlice>lambdaQuery().orderByDesc(AigcDocsSlice::getCreateTime)));
	}

	@GetMapping("/page")
	public R list(AigcDocsSlice data, QueryPage queryPage) {
		Page<AigcDocsSlice> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
		return R.ok(MybatisUtil.getData(docsSliceMapper.selectPage(page,
				Wrappers.<AigcDocsSlice>lambdaQuery()
					.eq(data.getKnowledgeId() != null, AigcDocsSlice::getKnowledgeId, data.getKnowledgeId())
					.eq(data.getDocsId() != null, AigcDocsSlice::getDocsId, data.getDocsId())
					.orderByDesc(AigcDocsSlice::getCreateTime))));
	}

	@GetMapping("/{id}")
	public R<AigcDocsSlice> findById(@PathVariable String id) {
		return R.ok(docsSliceMapper.selectById(id));
	}

	@PostMapping
	public R add(@RequestBody AigcDocsSlice data) {
		data.setCreateTime(new Date());
		docsSliceMapper.insert(data);
		return R.ok();
	}

	@PutMapping
	public R update(@RequestBody AigcDocsSlice data) {
		docsSliceMapper.updateById(data);
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		docsSliceMapper.deleteById(id);
		return R.ok();
	}

}
