/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libre.ai.modules.rag.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.libre.ai.modules.rag.service.AigcOssService;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/1/19
 */
@RequestMapping("/api/aigc/oss")
@RestController
@AllArgsConstructor
public class AigcOssController {

	private final AigcOssService aigcOssService;

	@GetMapping("/list")
	public R list() {
		List<AigcOss> list = aigcOssService
			.list(Wrappers.<AigcOss>lambdaQuery().eq(AigcOss::getUserId, "admin").orderByDesc(AigcOss::getCreateTime));
		return R.ok(list);
	}

	@PostMapping("/upload")
	public R upload(MultipartFile file) {
		return R.ok(aigcOssService.upload(file, "admin"));
	}

	@PutMapping
	public R update(@RequestBody AigcOss data) {
		aigcOssService.updateById(data);
		return R.ok();
	}

	@DeleteMapping("/{id}")
	public R delete(@PathVariable String id) {
		aigcOssService.removeById(id);
		return R.ok();
	}

}
