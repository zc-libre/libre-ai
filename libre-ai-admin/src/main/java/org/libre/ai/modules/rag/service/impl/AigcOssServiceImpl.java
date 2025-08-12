package org.libre.ai.modules.rag.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.config.AwsS3Config;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.libre.ai.modules.rag.mapper.AigcOssMapper;
import org.libre.ai.modules.rag.service.AigcOssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AigcOssServiceImpl extends ServiceImpl<AigcOssMapper, AigcOss> implements AigcOssService {

	private final S3Client s3Client;

	private final AwsS3Config awsS3Config;

	@Override
	public AigcOss upload(MultipartFile file, String userId) {
		log.info(">>>>>>>>>>>>>> OSS文件上传开始： {}", file.getOriginalFilename());

		try {
			String ossId = IdUtil.simpleUUID();
			String dateFolder = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
			String ext = FileUtil.extName(file.getOriginalFilename());
			String filename = ossId + "." + ext;
			String objectKey = dateFolder + "/" + filename;

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(awsS3Config.getBucketName())
				.key(objectKey)
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			GetUrlRequest getUrlRequest = GetUrlRequest.builder()
				.bucket(awsS3Config.getBucketName())
				.key(objectKey)
				.build();

			String url = s3Client.utilities().getUrl(getUrlRequest).toString();

			log.info(">>>>>>>>>>>>>> OSS文件上传结束： {} - {}", filename, url);

			AigcOss oss = new AigcOss();
			oss.setOssId(ossId);
			oss.setUrl(url);
			oss.setSize(file.getSize());
			oss.setFilename(filename);
			oss.setOriginalFilename(file.getOriginalFilename());
			oss.setBasePath(awsS3Config.getBucketName());
			oss.setPath(objectKey);
			oss.setExt(ext);
			oss.setContentType(file.getContentType());
			oss.setPlatform("AWS-S3");
			oss.setCreateTime(new Date());
			oss.setUserId(userId);

			this.save(oss);
			return oss;
		}
		catch (IOException e) {
			log.error("文件上传失败", e);
			throw new RuntimeException("文件上传失败: " + e.getMessage());
		}
	}

}
