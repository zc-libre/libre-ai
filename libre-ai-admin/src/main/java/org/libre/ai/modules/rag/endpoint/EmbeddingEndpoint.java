package org.libre.ai.modules.rag.endpoint;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.consts.EmbedConst;
import org.libre.ai.modules.rag.core.service.LangEmbeddingService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.EmbeddingResult;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.entity.AigcDocsSlice;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.libre.ai.modules.rag.mapper.AigcDocsMapper;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.service.AigcOssService;
import org.libre.ai.modules.rag.service.EmbeddingService;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Executors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/aigc/embedding")
public class EmbeddingEndpoint {

	private final LangEmbeddingService langEmbeddingService;

	private final AigcKnowledgeService aigcKnowledgeService;

	private final AigcDocsMapper aigcDocsMapper;

	private final AigcOssService aigcOssService;

	private final EmbeddingService embeddingService;

	@PostMapping("/text")
	public R text(@RequestBody AigcDocs data) {
		if (StrUtil.isBlankIfStr(data.getContent())) {
			throw new ServiceException("文档内容不能为空");
		}
		if (StrUtil.isBlank(data.getId())) {
			aigcKnowledgeService.addDocs(data);
		}
		data.setType(EmbedConst.ORIGIN_TYPE_INPUT).setSliceStatus(0);

		try {
			EmbeddingResult embeddingR = langEmbeddingService
				.embeddingText(new ChatRequest().setMessage(data.getContent())
					.setDocsName(data.getType())
					.setDocsId(data.getId())
					.setKnowledgeId(data.getKnowledgeId()));

			aigcKnowledgeService.addDocsSlice(new AigcDocsSlice().setKnowledgeId(data.getKnowledgeId())
				.setDocsId(data.getId())
				.setVectorId(embeddingR.getVectorId())
				.setName(data.getName())
				.setContent(embeddingR.getText()));

			aigcKnowledgeService.updateDocs(new AigcDocs().setId(data.getId()).setSliceStatus(1).setSliceNum(1));
		}
		catch (Exception e) {

			// del data
			aigcKnowledgeService.removeSlicesOfDoc(data.getId());
		}
		return R.ok();
	}

	@PostMapping("/docs/{knowledgeId}")
	// @SaCheckPermission("aigc:embedding:docs")
	public R docs(MultipartFile file, @PathVariable String knowledgeId) {
		String userId = "admin";
		AigcOss oss = aigcOssService.upload(file, userId);
		AigcDocs data = new AigcDocs().setName(oss.getOriginalFilename())
			.setSliceStatus(0)
			.setUrl(oss.getUrl())
			.setSize(file.getSize())
			.setType(EmbedConst.ORIGIN_TYPE_UPLOAD)
			.setKnowledgeId(knowledgeId);
		aigcKnowledgeService.addDocs(data);

		embeddingService.embedDocsSlice(data, oss.getUrl());
		return R.ok();
	}

	@GetMapping("/re-embed/{docsId}")
	public R reEmbed(@PathVariable String docsId) {
		String userId = "admin";
		AigcDocs docs = aigcDocsMapper.selectById(docsId);
		if (docs == null) {
			throw new ServiceException("没有查询到文档数据");
		}
		if (EmbedConst.ORIGIN_TYPE_INPUT.equals(docs.getType())) {
			text(docs);
		}
		if (EmbedConst.ORIGIN_TYPE_UPLOAD.equals(docs.getType())) {
			// clear before re-embed
			embeddingService.clearDocSlices(docsId);
			embeddingService.embedDocsSlice(docs, docs.getUrl());
		}
		return R.ok();
	}

	@PostMapping("/search")
	public R search(@RequestBody AigcDocs data) {
		log.info("Embedding search request - knowledgeId: {}, content: {}", data.getKnowledgeId(), data.getContent());
		return R.ok(embeddingService.search(data));
	}

}
