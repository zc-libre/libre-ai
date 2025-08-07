package org.libre.ai.modules.rag.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.libre.ai.modules.rag.mapper.AigcOssMapper;
import org.libre.ai.modules.rag.service.AigcOssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AigcOssServiceImpl extends ServiceImpl<AigcOssMapper, AigcOss> implements AigcOssService {

	// private final FileStorageService fileStorageService;

	@Override
	public AigcOss upload(MultipartFile file, String userId) {
		// log.info(">>>>>>>>>>>>>> OSS文件上传开始： {}", file.getOriginalFilename());
		// FileInfo info = fileStorageService.of(file)
		// .setPath(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN))
		// .upload();
		// log.info(">>>>>>>>>>>>>> OSS文件上传结束： {} - {}", info.getFilename(),
		// info.getUrl());
		// AigcOss oss = BeanUtil.copyProperties(info, AigcOss.class);
		// oss.setOssId(info.getId());
		// oss.setUserId(userId);
		// this.save(oss);
		return null;
	}

}
