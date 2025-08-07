<script lang="ts" setup>
import { computed, defineProps, ref } from 'vue';

interface Props {
  text?: string;
  loading?: boolean;
  error?: boolean;
  asRawText?: boolean;
  inversion?: boolean;
}

const props = defineProps<Props>();

const wrapClass = computed(() => {
  return [
    'text-wrap',
    'min-w-[20px]',
    'rounded-md',
    props.inversion ? 'bg-[#d2f9d1]' : 'bg-[#f4f6f8]',
    props.inversion ? 'dark:bg-[#a1dc95]' : 'dark:bg-[#1e1e20]',
    props.inversion ? 'message-request' : 'message-reply',
    { 'text-red-500': props.error }
  ];
});

const text = computed(() => {
  const value = props.text ?? '';
  if (!props.asRawText) {
    return value;
  }
  return value;
});
</script>

<template>
  <div class="text-black" :class="wrapClass">
    <div class="leading-relaxed break-words">
      <div
        v-if="!loading"
        class="markdown-body"
        :class="{ 'markdown-body-generate': loading }"
      >
        <div v-if="asRawText" class="whitespace-pre-wrap" v-text="text" />
        <div v-else class="markdown-body" v-html="text" />
      </div>
      <div v-else class="markdown-body">
        <div class="whitespace-pre-wrap" v-text="text" />
        <span class="loading-cursor">|</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.text-wrap {
  padding: 8px 12px;
  position: relative;
  overflow-wrap: break-word;
}

.message-request {
  margin-left: auto;
  margin-right: 0;
  max-width: 80%;
}

.message-reply {
  margin-left: 0;
  margin-right: auto;
  max-width: 80%;
}

.loading-cursor {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}

.markdown-body {
  word-wrap: break-word;
  line-height: 1.75;
  font-weight: 400;
  font-size: 14px;
  overflow-x: hidden;
}

.markdown-body p {
  margin: 0.8em 0;
}

.markdown-body pre {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  overflow: auto;
  line-height: 1.45;
  margin: 1em 0;
}

.markdown-body code {
  background-color: rgba(175, 184, 193, 0.2);
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-size: 85%;
}

.markdown-body pre code {
  background-color: transparent;
  padding: 0;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
  margin: 0.8em 0;
}

.markdown-body blockquote {
  margin: 0;
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
}

.markdown-body table {
  border-collapse: collapse;
  margin: 1em 0;
  overflow-x: auto;
}

.markdown-body th,
.markdown-body td {
  border: 1px solid #dfe2e5;
  padding: 6px 13px;
}

.markdown-body th {
  background-color: #f6f8fa;
  font-weight: 600;
}
</style>
