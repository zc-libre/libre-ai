package org.libre.ai.modules.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.entity.AigcDocsSlice;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.mapper.AigcDocsMapper;
import org.libre.ai.modules.rag.mapper.AigcDocsSliceMapper;
import org.libre.ai.modules.rag.mapper.AigcKnowledgeMapper;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcKnowledgeServiceImpl extends ServiceImpl<AigcKnowledgeMapper, AigcKnowledge>
		implements AigcKnowledgeService {

	private final AigcDocsMapper aigcDocsMapper;

	private final AigcDocsSliceMapper aigcDocsSliceMapper;

	@Override
	@Transactional
	public void addDocs(AigcDocs data) {
		data.setCreateTime(new Date());
		aigcDocsMapper.insert(data);
	}

	@Override
	@Transactional
	public void updateDocs(AigcDocs data) {
		aigcDocsMapper.updateById(data);
	}

	@Override
	@Transactional
	public void addDocsSlice(AigcDocsSlice data) {
		data.setCreateTime(new Date()).setWordNum(data.getContent().length()).setStatus(true);
		aigcDocsSliceMapper.insert(data);
	}

	@Override
	@Transactional
	public void updateDocsSlice(AigcDocsSlice data) {
		aigcDocsSliceMapper.updateById(data);
	}

	@Override
	public List<String> listSliceVectorIdsOfDoc(String docsId) {
		LambdaQueryWrapper<AigcDocsSlice> selectWrapper = Wrappers.<AigcDocsSlice>lambdaQuery()
			.select(AigcDocsSlice::getVectorId)
			.eq(AigcDocsSlice::getDocsId, docsId);
		List<String> vectorIds = aigcDocsSliceMapper.selectList(selectWrapper)
			.stream()
			.map(AigcDocsSlice::getVectorId)
			.toList();
		log.debug("slices of doc: [{}], count: [{}]", docsId, vectorIds.size());
		return vectorIds;
	}

	@Override
	public List<AigcDocs> getDocsByKb(String knowledgeId) {
		return aigcDocsMapper.selectList(Wrappers.<AigcDocs>lambdaQuery().eq(AigcDocs::getKnowledgeId, knowledgeId));
	}

	@Override
	@Transactional
	public void removeKnowledge(String knowledgeId) {
		baseMapper.deleteById(knowledgeId);
		// del docs & docsSlice
		List<String> docsIds = getDocsByKb(knowledgeId).stream().map(AigcDocs::getId).toList();
		docsIds.forEach(this::removeSlicesOfDoc);
	}

	@Override
	@Transactional
	public void removeSlicesOfDoc(String docsId) {
		LambdaQueryWrapper<AigcDocsSlice> deleteWrapper = Wrappers.<AigcDocsSlice>lambdaQuery()
			.eq(AigcDocsSlice::getDocsId, docsId);
		int count = aigcDocsSliceMapper.delete(deleteWrapper);
		log.debug("remove all slices of doc: [{}], count: [{}]", docsId, count);
	}

}
