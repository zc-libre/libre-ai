package org.libre.ai.modules.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.libre.ai.modules.rag.entity.AigcApp;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/7/26
 */
public interface AigcAppService extends IService<AigcApp> {

	List<AigcApp> list(AigcApp data);

	AigcApp getById(String id);

}
