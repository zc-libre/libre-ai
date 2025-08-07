package org.libre.ai.modules.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.springframework.web.multipart.MultipartFile;

public interface AigcOssService extends IService<AigcOss> {

	AigcOss upload(MultipartFile file, String userId);

}
