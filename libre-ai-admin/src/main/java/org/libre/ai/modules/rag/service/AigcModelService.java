package org.libre.ai.modules.rag.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.utils.QueryPage;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/1/19
 */
public interface AigcModelService extends IService<AigcModel> {

	List<AigcModel> getChatModels();

	List<AigcModel> getImageModels();

	List<AigcModel> getEmbeddingModels();

	List<AigcModel> list(AigcModel data);

	Page<AigcModel> page(AigcModel data, QueryPage queryPage);

	AigcModel selectById(String id);

}
