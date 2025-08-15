<script lang="ts" setup>
import { ElAlert } from 'element-plus';
import hljs from 'highlight.js';
import javascript from 'highlight.js/lib/languages/javascript';
import 'highlight.js/styles/atom-one-dark.css';

hljs.registerLanguage('javascript', javascript);

const url = `http://langchat.cn`;
const request = `POST /v1/chat/completions HTTP/1.1
Content-Type: application/json
Authorization: 'Bearer YOUR_ACCESS_TOKEN'
Body:
{
    "messages": [
        { "role": "user", "content": "你好" }
    ]
}`;

const response = `data: {"choices": [{"index": 0, "delta": {"content": "你好！"}, "finish_reason": null}], "session_id": null}

data: {"choices": [{"index": 0, "delta": {"content": "我能"}, "finish_reason": null}], "session_id": null}

data: {"choices": [{"index": 0, "delta": {"content": "为你"}, "finish_reason": null}], "session_id": null}

data: {"choices": [{"index": 0, "delta": {"content": "做些什么？"}, "finish_reason": null}], "session_id": null}

data: {"choices": [{"index": 0, "delta": {}, "finish_reason": "stop", "usage": {"prompt_tokens": 9, "completion_tokens": 6, "total_tokens": 15}}], "session_id": null}`;

const demo = `const url = 'http://langchat.cn/v1/chat/completions';
const data = {
    "messages": [
        { "role": "user", "content": "你好" }
    ]
};

fetch(url, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
  },
  body: JSON.stringify(data)
})
.then(response => {
  if (!response.ok) {
    throw new Error('Network response was not ok ' + response.statusText);
  }
  return response.json();
})
.then(jsonData => {
  console.log('Success:', jsonData);
})
.catch(error => {
  console.error('Error:', error);
});`;

// 高亮代码
function highlightCode(code: string, language: string = 'javascript') {
  return hljs.highlight(code, { language }).value;
}
</script>

<template>
  <div class="p-4 bg-white h-full overflow-auto rounded">
    <div class="flex flex-col gap-4">
      <div>
        <ElAlert
          title="API URL（API接口格式遵循OpenAI格式）"
          type="info"
          :closable="false"
        />
        <div class="bg-[#18181c] mt-2 py-2 px-4 overflow-x-auto rounded">
          <pre><code class="text-white" v-html="highlightCode(url)" /></pre>
        </div>
      </div>

      <div>
        <ElAlert title="Request" type="info" :closable="false" />
        <div class="bg-[#18181c] mt-2 py-2 px-4 overflow-x-auto rounded">
          <pre><code class="text-white" v-html="highlightCode(request)" /></pre>
        </div>
      </div>

      <div>
        <ElAlert title="Response（Stream）" type="info" :closable="false" />
        <div class="bg-[#18181c] py-2 mt-2 px-4 overflow-x-auto rounded">
          <pre><code class="text-white" v-html="highlightCode(response)" /></pre>
        </div>
      </div>

      <div>
        <ElAlert title="API请求示例" type="info" :closable="false" />
        <div class="bg-[#18181c] mt-2 py-2 px-4 overflow-x-auto rounded">
          <pre><code class="text-white" v-html="highlightCode(demo)" /></pre>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
pre {
  margin: 0;
  word-wrap: break-word;
  white-space: pre-wrap;
}

code {
  font-family: Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
}
</style>
