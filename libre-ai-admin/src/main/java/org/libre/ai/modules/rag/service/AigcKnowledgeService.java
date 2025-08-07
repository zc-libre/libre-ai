package org.libre.ai.modules.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.entity.AigcDocsSlice;
import org.libre.ai.modules.rag.entity.AigcKnowledge;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
public interface AigcKnowledgeService extends IService<AigcKnowledge> {

	void addDocs(AigcDocs data);

	void updateDocs(AigcDocs data);

	void addDocsSlice(AigcDocsSlice data);

	void updateDocsSlice(AigcDocsSlice data);

	List<String> listSliceVectorIdsOfDoc(String docsId);

	List<AigcDocs> getDocsByKb(String knowledgeId);

	void removeKnowledge(String knowledgeId);

	void removeSlicesOfDoc(String docsId);

}
