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

package org.libre.ai.modules.rag.core.provider.build;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import org.libre.ai.modules.rag.entity.AigcModel;

/**
 * @author GB
 * @since 2024-08-18 09:57
 */
public interface ModelBuildHandler {

	/**
	 * 判断是不是当前模型
	 */
	boolean whetherCurrentModel(AigcModel model);

	/**
	 * basic check
	 */
	boolean basicCheck(AigcModel model);

	/**
	 * streaming chat build
	 */
	StreamingChatModel buildStreamingChat(AigcModel model);

	/**
	 * chat build
	 */
	ChatModel buildChatLanguageModel(AigcModel model);

	/**
	 * embedding config
	 */
	EmbeddingModel buildEmbedding(AigcModel model);

	/**
	 * image config
	 */
	ImageModel buildImage(AigcModel model);

}
