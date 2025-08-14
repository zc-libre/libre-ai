package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class AigcModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -19545329638997333L;

	@TableId(type = IdType.ASSIGN_UUID)
	private String id;

	private String type;

	private String model;

	private String provider;

	private String name;

	private Integer responseLimit;

	private Double temperature = 1.0;

	private Double topP = 0.0;

	private String apiKey;

	private String secretKey;

	private String baseUrl;

	private String endpoint;

	private String geminiLocation;

	private String geminiProject;

	private String azureDeploymentName;

	private String imageSize;

	private String imageQuality;

	private String imageStyle;

	private Integer dimension;

	@Override
	public String toString() {
		return "AigcModel{" +
			"id='" + id + '\'' +
			", type='" + type + '\'' +
			", model='" + model + '\'' +
			", provider='" + provider + '\'' +
			", name='" + name + '\'' +
			", responseLimit=" + responseLimit +
			", temperature=" + temperature +
			", topP=" + topP +
			", apiKey='" + maskSensitiveData(apiKey) + '\'' +
			", secretKey='" + maskSensitiveData(secretKey) + '\'' +
			", baseUrl='" + baseUrl + '\'' +
			", endpoint='" + endpoint + '\'' +
			", geminiLocation='" + geminiLocation + '\'' +
			", geminiProject='" + geminiProject + '\'' +
			", azureDeploymentName='" + azureDeploymentName + '\'' +
			", imageSize='" + imageSize + '\'' +
			", imageQuality='" + imageQuality + '\'' +
			", imageStyle='" + imageStyle + '\'' +
			", dimension=" + dimension +
			'}';
	}

	private String maskSensitiveData(String data) {
		if (data == null || data.length() <= 8) {
			return "****";
		}
		return data.substring(0, 4) + "****" + data.substring(data.length() - 4);
	}

}
