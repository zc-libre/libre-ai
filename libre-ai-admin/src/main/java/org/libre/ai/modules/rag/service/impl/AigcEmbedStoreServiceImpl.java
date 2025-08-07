package org.libre.ai.modules.rag.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcEmbedStore;
import org.libre.ai.modules.rag.mapper.AigcEmbedStoreMapper;
import org.libre.ai.modules.rag.service.AigcEmbedStoreService;
import org.springframework.stereotype.Service;

/**
 * @author tycoding
 * @since 2024/10/28
 */
@Service
@RequiredArgsConstructor
public class AigcEmbedStoreServiceImpl extends ServiceImpl<AigcEmbedStoreMapper, AigcEmbedStore>
		implements AigcEmbedStoreService {

}
