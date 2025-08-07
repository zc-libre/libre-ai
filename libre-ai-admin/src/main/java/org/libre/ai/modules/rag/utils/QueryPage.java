package org.libre.ai.modules.rag.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tycoding
 * @since 2024/1/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前页
	 */
	private int page = 1;

	/**
	 * 每页的记录数
	 */
	private int limit = 10;

}
