#!/usr/bin/env node

/**
 * TailwindCSS完整离线版本构建脚本
 * 生成包含所有可能类的完整TailwindCSS文件，支持AI生成的不确定代码
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'
import postcss from 'postcss'
import tailwindcss from 'tailwindcss'
import autoprefixer from 'autoprefixer'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.join(__dirname, '..')
const libsDir = path.join(projectRoot, 'src/assets/preview-libs')

/**
 * 创建包含所有可能类的TailwindCSS输入
 * 涵盖AI可能生成的所有常用类名组合
 */
function createComprehensiveInput() {
  return `
@tailwind base;
@tailwind components;
@tailwind utilities;

/* 
 * 为了确保包含所有可能的TailwindCSS类，我们在这里列出全面的类模式
 * 这样AI生成的任何代码都能正常工作
 */

/* 强制包含所有基础类 - 通过safelist保证生成 */
.force-include {
  /* 间距 - 0到96的所有值 */
  @apply p-0 p-px p-0.5 p-1 p-1.5 p-2 p-2.5 p-3 p-3.5 p-4 p-5 p-6 p-7 p-8 p-9 p-10;
  @apply p-11 p-12 p-14 p-16 p-20 p-24 p-28 p-32 p-36 p-40 p-44 p-48 p-52 p-56 p-60 p-64;
  @apply p-72 p-80 p-96;
  
  @apply pt-0 pt-px pt-0.5 pt-1 pt-1.5 pt-2 pt-2.5 pt-3 pt-3.5 pt-4 pt-5 pt-6 pt-7 pt-8;
  @apply pr-0 pr-px pr-0.5 pr-1 pr-1.5 pr-2 pr-2.5 pr-3 pr-3.5 pr-4 pr-5 pr-6 pr-7 pr-8;
  @apply pb-0 pb-px pb-0.5 pb-1 pb-1.5 pb-2 pb-2.5 pb-3 pb-3.5 pb-4 pb-5 pb-6 pb-7 pb-8;
  @apply pl-0 pl-px pl-0.5 pl-1 pl-1.5 pl-2 pl-2.5 pl-3 pl-3.5 pl-4 pl-5 pl-6 pl-7 pl-8;
  @apply px-0 px-px px-0.5 px-1 px-1.5 px-2 px-2.5 px-3 px-3.5 px-4 px-5 px-6 px-7 px-8;
  @apply py-0 py-px py-0.5 py-1 py-1.5 py-2 py-2.5 py-3 py-3.5 py-4 py-5 py-6 py-7 py-8;
  
  @apply m-0 m-px m-0.5 m-1 m-1.5 m-2 m-2.5 m-3 m-3.5 m-4 m-5 m-6 m-7 m-8 m-9 m-10;
  @apply m-11 m-12 m-14 m-16 m-20 m-24 m-28 m-32 m-36 m-40 m-44 m-48 m-52 m-56 m-60 m-64;
  @apply m-72 m-80 m-96 m-auto;
  
  @apply mt-0 mt-px mt-0.5 mt-1 mt-1.5 mt-2 mt-2.5 mt-3 mt-3.5 mt-4 mt-5 mt-6 mt-7 mt-8;
  @apply mr-0 mr-px mr-0.5 mr-1 mr-1.5 mr-2 mr-2.5 mr-3 mr-3.5 mr-4 mr-5 mr-6 mr-7 mr-8;
  @apply mb-0 mb-px mb-0.5 mb-1 mb-1.5 mb-2 mb-2.5 mb-3 mb-3.5 mb-4 mb-5 mb-6 mb-7 mb-8;
  @apply ml-0 ml-px ml-0.5 ml-1 ml-1.5 ml-2 ml-2.5 ml-3 ml-3.5 ml-4 ml-5 ml-6 ml-7 ml-8;
  @apply mx-0 mx-px mx-0.5 mx-1 mx-1.5 mx-2 mx-2.5 mx-3 mx-3.5 mx-4 mx-5 mx-6 mx-7 mx-8;
  @apply my-0 my-px my-0.5 my-1 my-1.5 my-2 my-2.5 my-3 my-3.5 my-4 my-5 my-6 my-7 my-8;
  @apply mx-auto my-auto;
  
  /* 颜色 - 所有预设颜色和色阶 */
  @apply text-black text-white;
  @apply text-gray-50 text-gray-100 text-gray-200 text-gray-300 text-gray-400 text-gray-500;
  @apply text-gray-600 text-gray-700 text-gray-800 text-gray-900 text-gray-950;
  @apply text-red-50 text-red-100 text-red-200 text-red-300 text-red-400 text-red-500;
  @apply text-red-600 text-red-700 text-red-800 text-red-900 text-red-950;
  @apply text-blue-50 text-blue-100 text-blue-200 text-blue-300 text-blue-400 text-blue-500;
  @apply text-blue-600 text-blue-700 text-blue-800 text-blue-900 text-blue-950;
  @apply text-green-50 text-green-100 text-green-200 text-green-300 text-green-400 text-green-500;
  @apply text-green-600 text-green-700 text-green-800 text-green-900 text-green-950;
  @apply text-yellow-50 text-yellow-100 text-yellow-200 text-yellow-300 text-yellow-400 text-yellow-500;
  @apply text-yellow-600 text-yellow-700 text-yellow-800 text-yellow-900 text-yellow-950;
  @apply text-purple-50 text-purple-100 text-purple-200 text-purple-300 text-purple-400 text-purple-500;
  @apply text-purple-600 text-purple-700 text-purple-800 text-purple-900 text-purple-950;
  @apply text-pink-50 text-pink-100 text-pink-200 text-pink-300 text-pink-400 text-pink-500;
  @apply text-pink-600 text-pink-700 text-pink-800 text-pink-900 text-pink-950;
  @apply text-indigo-50 text-indigo-100 text-indigo-200 text-indigo-300 text-indigo-400 text-indigo-500;
  @apply text-indigo-600 text-indigo-700 text-indigo-800 text-indigo-900 text-indigo-950;
  
  @apply bg-black bg-white bg-transparent;
  @apply bg-gray-50 bg-gray-100 bg-gray-200 bg-gray-300 bg-gray-400 bg-gray-500;
  @apply bg-gray-600 bg-gray-700 bg-gray-800 bg-gray-900 bg-gray-950;
  @apply bg-red-50 bg-red-100 bg-red-200 bg-red-300 bg-red-400 bg-red-500;
  @apply bg-red-600 bg-red-700 bg-red-800 bg-red-900 bg-red-950;
  @apply bg-blue-50 bg-blue-100 bg-blue-200 bg-blue-300 bg-blue-400 bg-blue-500;
  @apply bg-blue-600 bg-blue-700 bg-blue-800 bg-blue-900 bg-blue-950;
  @apply bg-green-50 bg-green-100 bg-green-200 bg-green-300 bg-green-400 bg-green-500;
  @apply bg-green-600 bg-green-700 bg-green-800 bg-green-900 bg-green-950;
  @apply bg-yellow-50 bg-yellow-100 bg-yellow-200 bg-yellow-300 bg-yellow-400 bg-yellow-500;
  @apply bg-yellow-600 bg-yellow-700 bg-yellow-800 bg-yellow-900 bg-yellow-950;
  @apply bg-purple-50 bg-purple-100 bg-purple-200 bg-purple-300 bg-purple-400 bg-purple-500;
  @apply bg-purple-600 bg-purple-700 bg-purple-800 bg-purple-900 bg-purple-950;
  @apply bg-pink-50 bg-pink-100 bg-pink-200 bg-pink-300 bg-pink-400 bg-pink-500;
  @apply bg-pink-600 bg-pink-700 bg-pink-800 bg-pink-900 bg-pink-950;
  @apply bg-indigo-50 bg-indigo-100 bg-indigo-200 bg-indigo-300 bg-indigo-400 bg-indigo-500;
  @apply bg-indigo-600 bg-indigo-700 bg-indigo-800 bg-indigo-900 bg-indigo-950;
  
  /* 边框颜色 */
  @apply border-gray-200 border-gray-300 border-gray-400 border-gray-500 border-gray-600;
  @apply border-red-200 border-red-300 border-red-400 border-red-500 border-red-600;
  @apply border-blue-200 border-blue-300 border-blue-400 border-blue-500 border-blue-600;
  @apply border-green-200 border-green-300 border-green-400 border-green-500 border-green-600;
  
  /* 布局 */
  @apply block inline inline-block flex inline-flex grid inline-grid;
  @apply hidden table table-row table-cell;
  @apply flow-root contents list-item;
  
  /* Flexbox */
  @apply flex-row flex-row-reverse flex-col flex-col-reverse;
  @apply flex-wrap flex-wrap-reverse flex-nowrap;
  @apply items-start items-end items-center items-baseline items-stretch;
  @apply justify-start justify-end justify-center justify-between justify-around justify-evenly;
  @apply content-start content-end content-center content-between content-around content-evenly;
  @apply self-auto self-start self-end self-center self-stretch self-baseline;
  @apply flex-1 flex-auto flex-initial flex-none;
  @apply grow grow-0 shrink shrink-0;
  
  /* Grid */
  @apply grid-cols-1 grid-cols-2 grid-cols-3 grid-cols-4 grid-cols-5 grid-cols-6;
  @apply grid-cols-7 grid-cols-8 grid-cols-9 grid-cols-10 grid-cols-11 grid-cols-12;
  @apply grid-cols-none grid-cols-subgrid;
  @apply col-auto col-span-1 col-span-2 col-span-3 col-span-4 col-span-5 col-span-6;
  @apply col-span-7 col-span-8 col-span-9 col-span-10 col-span-11 col-span-12 col-span-full;
  @apply grid-rows-1 grid-rows-2 grid-rows-3 grid-rows-4 grid-rows-5 grid-rows-6;
  @apply row-auto row-span-1 row-span-2 row-span-3 row-span-4 row-span-5 row-span-6 row-span-full;
  @apply gap-0 gap-x-0 gap-y-0 gap-px gap-x-px gap-y-px;
  @apply gap-0.5 gap-x-0.5 gap-y-0.5 gap-1 gap-x-1 gap-y-1;
  @apply gap-1.5 gap-x-1.5 gap-y-1.5 gap-2 gap-x-2 gap-y-2;
  @apply gap-2.5 gap-x-2.5 gap-y-2.5 gap-3 gap-x-3 gap-y-3;
  @apply gap-3.5 gap-x-3.5 gap-y-3.5 gap-4 gap-x-4 gap-y-4;
  @apply gap-5 gap-x-5 gap-y-5 gap-6 gap-x-6 gap-y-6;
  @apply gap-7 gap-x-7 gap-y-7 gap-8 gap-x-8 gap-y-8;
  
  /* 定位 */
  @apply static relative absolute fixed sticky;
  @apply inset-0 inset-x-0 inset-y-0 inset-auto;
  @apply top-0 right-0 bottom-0 left-0;
  @apply top-auto right-auto bottom-auto left-auto;
  @apply top-1/2 right-1/2 bottom-1/2 left-1/2;
  @apply top-full right-full bottom-full left-full;
  
  /* 尺寸 */
  @apply w-0 w-px w-0.5 w-1 w-1.5 w-2 w-2.5 w-3 w-3.5 w-4 w-5 w-6 w-7 w-8 w-9 w-10;
  @apply w-11 w-12 w-14 w-16 w-20 w-24 w-28 w-32 w-36 w-40 w-44 w-48 w-52 w-56 w-60 w-64;
  @apply w-72 w-80 w-96 w-auto w-1/2 w-1/3 w-2/3 w-1/4 w-2/4 w-3/4 w-1/5 w-2/5 w-3/5 w-4/5;
  @apply w-1/6 w-2/6 w-3/6 w-4/6 w-5/6 w-1/12 w-2/12 w-3/12 w-4/12 w-5/12 w-6/12;
  @apply w-7/12 w-8/12 w-9/12 w-10/12 w-11/12 w-full w-screen w-min w-max w-fit;
  
  @apply h-0 h-px h-0.5 h-1 h-1.5 h-2 h-2.5 h-3 h-3.5 h-4 h-5 h-6 h-7 h-8 h-9 h-10;
  @apply h-11 h-12 h-14 h-16 h-20 h-24 h-28 h-32 h-36 h-40 h-44 h-48 h-52 h-56 h-60 h-64;
  @apply h-72 h-80 h-96 h-auto h-1/2 h-1/3 h-2/3 h-1/4 h-2/4 h-3/4 h-1/5 h-2/5 h-3/5 h-4/5;
  @apply h-1/6 h-2/6 h-3/6 h-4/6 h-5/6 h-full h-screen h-min h-max h-fit;
  
  @apply min-w-0 min-w-full min-w-min min-w-max min-w-fit;
  @apply min-h-0 min-h-full min-h-screen min-h-min min-h-max min-h-fit;
  @apply max-w-0 max-w-none max-w-xs max-w-sm max-w-md max-w-lg max-w-xl max-w-2xl;
  @apply max-w-3xl max-w-4xl max-w-5xl max-w-6xl max-w-7xl max-w-full;
  @apply max-w-min max-w-max max-w-fit max-w-prose;
  @apply max-h-0 max-h-px max-h-0.5 max-h-1 max-h-1.5 max-h-2 max-h-2.5 max-h-3;
  @apply max-h-full max-h-screen max-h-min max-h-max max-h-fit;
  
  /* 字体 */
  @apply text-xs text-sm text-base text-lg text-xl text-2xl text-3xl text-4xl text-5xl text-6xl;
  @apply font-thin font-extralight font-light font-normal font-medium font-semibold font-bold;
  @apply font-extrabold font-black;
  @apply italic not-italic;
  @apply text-left text-center text-right text-justify;
  @apply underline overline line-through no-underline;
  @apply uppercase lowercase capitalize normal-case;
  @apply leading-3 leading-4 leading-5 leading-6 leading-7 leading-8 leading-9 leading-10;
  @apply leading-none leading-tight leading-snug leading-normal leading-relaxed leading-loose;
  
  /* 边框 */
  @apply border border-0 border-2 border-4 border-8;
  @apply border-t border-r border-b border-l;
  @apply border-t-0 border-r-0 border-b-0 border-l-0;
  @apply border-t-2 border-r-2 border-b-2 border-l-2;
  @apply border-t-4 border-r-4 border-b-4 border-l-4;
  @apply border-t-8 border-r-8 border-b-8 border-l-8;
  @apply border-solid border-dashed border-dotted border-double border-hidden border-none;
  @apply rounded rounded-none rounded-sm rounded-md rounded-lg rounded-xl rounded-2xl rounded-3xl;
  @apply rounded-full;
  @apply rounded-t rounded-r rounded-b rounded-l;
  @apply rounded-tl rounded-tr rounded-br rounded-bl;
  
  /* 阴影 */
  @apply shadow-sm shadow shadow-md shadow-lg shadow-xl shadow-2xl shadow-inner shadow-none;
  @apply drop-shadow-sm drop-shadow drop-shadow-md drop-shadow-lg drop-shadow-xl;
  @apply drop-shadow-2xl drop-shadow-none;
  
  /* 透明度 */
  @apply opacity-0 opacity-5 opacity-10 opacity-20 opacity-25 opacity-30 opacity-40;
  @apply opacity-50 opacity-60 opacity-70 opacity-75 opacity-80 opacity-90 opacity-95 opacity-100;
  
  /* 过渡 */
  @apply transition-none transition-all transition transition-colors transition-opacity;
  @apply transition-shadow transition-transform;
  @apply duration-75 duration-100 duration-150 duration-200 duration-300 duration-500;
  @apply duration-700 duration-1000;
  @apply ease-linear ease-in ease-out ease-in-out;
  
  /* 变换 */
  @apply transform transform-none;
  @apply scale-0 scale-50 scale-75 scale-90 scale-95 scale-100 scale-105 scale-110 scale-125 scale-150;
  @apply rotate-0 rotate-1 rotate-2 rotate-3 rotate-6 rotate-12 rotate-45 rotate-90 rotate-180;
  @apply translate-x-0 translate-x-1 translate-x-2 translate-x-3 translate-x-4;
  @apply translate-y-0 translate-y-1 translate-y-2 translate-y-3 translate-y-4;
  @apply skew-x-0 skew-x-1 skew-x-2 skew-x-3 skew-x-6 skew-x-12;
  @apply skew-y-0 skew-y-1 skew-y-2 skew-y-3 skew-y-6 skew-y-12;
  
  /* 其他常用 */
  @apply cursor-auto cursor-default cursor-pointer cursor-wait cursor-text cursor-move;
  @apply cursor-help cursor-not-allowed cursor-none cursor-context-menu cursor-progress;
  @apply cursor-cell cursor-crosshair cursor-vertical-text cursor-alias cursor-copy;
  @apply select-none select-text select-all select-auto;
  @apply resize-none resize-y resize-x resize;
  @apply overflow-auto overflow-hidden overflow-clip overflow-visible overflow-scroll;
  @apply overflow-x-auto overflow-x-hidden overflow-x-clip overflow-x-visible overflow-x-scroll;
  @apply overflow-y-auto overflow-y-hidden overflow-y-clip overflow-y-visible overflow-y-scroll;
  @apply whitespace-normal whitespace-nowrap whitespace-pre whitespace-pre-line whitespace-pre-wrap;
  @apply break-normal break-words break-all break-keep;
  @apply z-0 z-10 z-20 z-30 z-40 z-50 z-auto;
}

/* 响应式前缀 */
@media (min-width: 640px) {
  .sm-safelist {
    @apply sm:block sm:flex sm:grid sm:hidden;
    @apply sm:p-0 sm:p-1 sm:p-2 sm:p-3 sm:p-4 sm:p-5 sm:p-6 sm:p-8;
    @apply sm:m-0 sm:m-1 sm:m-2 sm:m-3 sm:m-4 sm:m-5 sm:m-6 sm:m-8;
    @apply sm:w-full sm:w-1/2 sm:w-1/3 sm:w-1/4 sm:w-auto;
    @apply sm:h-full sm:h-1/2 sm:h-1/3 sm:h-1/4 sm:h-auto;
    @apply sm:text-left sm:text-center sm:text-right;
    @apply sm:justify-start sm:justify-center sm:justify-end sm:justify-between;
    @apply sm:items-start sm:items-center sm:items-end;
  }
}

@media (min-width: 768px) {
  .md-safelist {
    @apply md:block md:flex md:grid md:hidden;
    @apply md:p-0 md:p-1 md:p-2 md:p-3 md:p-4 md:p-5 md:p-6 md:p-8;
    @apply md:m-0 md:m-1 md:m-2 md:m-3 md:m-4 md:m-5 md:m-6 md:m-8;
    @apply md:w-full md:w-1/2 md:w-1/3 md:w-1/4 md:w-auto;
    @apply md:h-full md:h-1/2 md:h-1/3 md:h-1/4 md:h-auto;
    @apply md:text-left md:text-center md:text-right;
    @apply md:justify-start md:justify-center md:justify-end md:justify-between;
    @apply md:items-start md:items-center md:items-end;
    @apply md:grid-cols-1 md:grid-cols-2 md:grid-cols-3 md:grid-cols-4;
  }
}

@media (min-width: 1024px) {
  .lg-safelist {
    @apply lg:block lg:flex lg:grid lg:hidden;
    @apply lg:p-0 lg:p-1 lg:p-2 lg:p-3 lg:p-4 lg:p-5 lg:p-6 lg:p-8;
    @apply lg:m-0 lg:m-1 lg:m-2 lg:m-3 lg:m-4 lg:m-5 lg:m-6 lg:m-8;
    @apply lg:w-full lg:w-1/2 lg:w-1/3 lg:w-1/4 lg:w-auto;
    @apply lg:h-full lg:h-1/2 lg:h-1/3 lg:h-1/4 lg:h-auto;
    @apply lg:text-left lg:text-center lg:text-right;
    @apply lg:justify-start lg:justify-center lg:justify-end lg:justify-between;
    @apply lg:items-start lg:items-center lg:items-end;
    @apply lg:grid-cols-1 lg:grid-cols-2 lg:grid-cols-3 lg:grid-cols-4 lg:grid-cols-5 lg:grid-cols-6;
  }
}

@media (min-width: 1280px) {
  .xl-safelist {
    @apply xl:block xl:flex xl:grid xl:hidden;
    @apply xl:p-0 xl:p-1 xl:p-2 xl:p-3 xl:p-4 xl:p-5 xl:p-6 xl:p-8;
    @apply xl:m-0 xl:m-1 xl:m-2 xl:m-3 xl:m-4 xl:m-5 xl:m-6 xl:m-8;
    @apply xl:w-full xl:w-1/2 xl:w-1/3 xl:w-1/4 xl:w-auto;
    @apply xl:h-full xl:h-1/2 xl:h-1/3 xl:h-1/4 xl:h-auto;
    @apply xl:text-left xl:text-center xl:text-right;
    @apply xl:justify-start xl:justify-center xl:justify-end xl:justify-between;
    @apply xl:items-start xl:items-center xl:items-end;
    @apply xl:grid-cols-1 xl:grid-cols-2 xl:grid-cols-3 xl:grid-cols-4 xl:grid-cols-5 xl:grid-cols-6;
  }
}

/* 状态变体 */
.state-safelist:hover {
  @apply hover:bg-gray-100 hover:bg-blue-100 hover:bg-red-100 hover:bg-green-100;
  @apply hover:text-gray-800 hover:text-blue-800 hover:text-red-800 hover:text-green-800;
  @apply hover:border-gray-300 hover:border-blue-300 hover:border-red-300 hover:border-green-300;
  @apply hover:shadow-md hover:shadow-lg hover:scale-105;
}

.state-safelist:focus {
  @apply focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50;
  @apply focus:border-blue-500 focus:bg-blue-50;
}

.state-safelist:active {
  @apply active:bg-gray-200 active:bg-blue-200 active:bg-red-200 active:bg-green-200;
  @apply active:scale-95 active:transform;
}

/* 暗色模式 */
@media (prefers-color-scheme: dark) {
  .dark-safelist {
    @apply dark:bg-gray-900 dark:bg-gray-800 dark:bg-gray-700;
    @apply dark:text-white dark:text-gray-100 dark:text-gray-200 dark:text-gray-300;
    @apply dark:border-gray-600 dark:border-gray-700 dark:border-gray-800;
  }
}
`
}

/**
 * 创建TailwindCSS配置，确保包含所有类
 */
function createFullTailwindConfig() {
  return {
    content: [
      // 虚拟内容，包含所有可能的类模式
      {
        raw: `
        <!-- 所有可能的类模式 -->
        <div class="p-0 p-px p-0.5 p-1 p-1.5 p-2 p-2.5 p-3 p-3.5 p-4 p-5 p-6 p-7 p-8 p-9 p-10 p-11 p-12 p-14 p-16 p-20 p-24 p-28 p-32 p-36 p-40 p-44 p-48 p-52 p-56 p-60 p-64 p-72 p-80 p-96">
        <div class="m-0 m-px m-0.5 m-1 m-1.5 m-2 m-2.5 m-3 m-3.5 m-4 m-5 m-6 m-7 m-8 m-9 m-10 m-11 m-12 m-14 m-16 m-20 m-24 m-28 m-32 m-36 m-40 m-44 m-48 m-52 m-56 m-60 m-64 m-72 m-80 m-96 m-auto">
        <div class="text-black text-white text-gray-50 text-gray-100 text-gray-200 text-gray-300 text-gray-400 text-gray-500 text-gray-600 text-gray-700 text-gray-800 text-gray-900 text-gray-950">
        <div class="bg-black bg-white bg-transparent bg-gray-50 bg-gray-100 bg-gray-200 bg-gray-300 bg-gray-400 bg-gray-500 bg-gray-600 bg-gray-700 bg-gray-800 bg-gray-900 bg-gray-950">
        <div class="text-red-50 text-red-100 text-red-200 text-red-300 text-red-400 text-red-500 text-red-600 text-red-700 text-red-800 text-red-900 text-red-950">
        <div class="bg-red-50 bg-red-100 bg-red-200 bg-red-300 bg-red-400 bg-red-500 bg-red-600 bg-red-700 bg-red-800 bg-red-900 bg-red-950">
        <div class="text-blue-50 text-blue-100 text-blue-200 text-blue-300 text-blue-400 text-blue-500 text-blue-600 text-blue-700 text-blue-800 text-blue-900 text-blue-950">
        <div class="bg-blue-50 bg-blue-100 bg-blue-200 bg-blue-300 bg-blue-400 bg-blue-500 bg-blue-600 bg-blue-700 bg-blue-800 bg-blue-900 bg-blue-950">
        `,
        extension: 'html'
      }
    ],
    
    // 使用 safelist 确保所有类都被包含
    safelist: [
      // 间距相关
      {
        pattern: /^(p|m|pt|pr|pb|pl|px|py|mt|mr|mb|ml|mx|my)-(0|px|0\.5|1|1\.5|2|2\.5|3|3\.5|4|5|6|7|8|9|10|11|12|14|16|20|24|28|32|36|40|44|48|52|56|60|64|72|80|96|auto)$/
      },
      
      // 颜色相关
      {
        pattern: /^(text|bg|border)-(black|white|transparent|gray|red|orange|amber|yellow|lime|green|emerald|teal|cyan|sky|blue|indigo|violet|purple|fuchsia|pink|rose)-(50|100|200|300|400|500|600|700|800|900|950)$/
      },
      
      // 布局相关
      {
        pattern: /^(block|inline|inline-block|flex|inline-flex|grid|inline-grid|hidden|table|table-row|table-cell|flow-root|contents|list-item)$/
      },
      
      // Flexbox
      {
        pattern: /^(flex-row|flex-row-reverse|flex-col|flex-col-reverse|flex-wrap|flex-wrap-reverse|flex-nowrap)$/
      },
      {
        pattern: /^(items|justify|content|self)-(start|end|center|baseline|stretch|between|around|evenly|auto)$/
      },
      {
        pattern: /^(flex-1|flex-auto|flex-initial|flex-none|grow|grow-0|shrink|shrink-0)$/
      },
      
      // Grid
      {
        pattern: /^grid-cols-(1|2|3|4|5|6|7|8|9|10|11|12|none|subgrid)$/
      },
      {
        pattern: /^col-(auto|span-1|span-2|span-3|span-4|span-5|span-6|span-7|span-8|span-9|span-10|span-11|span-12|span-full)$/
      },
      {
        pattern: /^grid-rows-(1|2|3|4|5|6|none|subgrid)$/
      },
      {
        pattern: /^row-(auto|span-1|span-2|span-3|span-4|span-5|span-6|span-full)$/
      },
      {
        pattern: /^gap(-x|-y)?-(0|px|0\.5|1|1\.5|2|2\.5|3|3\.5|4|5|6|7|8|9|10|11|12|14|16|20|24|28|32|36|40|44|48|52|56|60|64|72|80|96)$/
      },
      
      // 定位
      {
        pattern: /^(static|relative|absolute|fixed|sticky)$/
      },
      {
        pattern: /^(inset|inset-x|inset-y|top|right|bottom|left)-(0|auto|px|0\.5|1|1\.5|2|2\.5|3|3\.5|4|5|6|7|8|9|10|11|12|14|16|20|24|28|32|36|40|44|48|52|56|60|64|72|80|96|1\/2|1\/3|2\/3|1\/4|2\/4|3\/4|full)$/
      },
      
      // 尺寸
      {
        pattern: /^(w|h)-(0|px|0\.5|1|1\.5|2|2\.5|3|3\.5|4|5|6|7|8|9|10|11|12|14|16|20|24|28|32|36|40|44|48|52|56|60|64|72|80|96|auto|1\/2|1\/3|2\/3|1\/4|2\/4|3\/4|1\/5|2\/5|3\/5|4\/5|1\/6|2\/6|3\/6|4\/6|5\/6|1\/12|2\/12|3\/12|4\/12|5\/12|6\/12|7\/12|8\/12|9\/12|10\/12|11\/12|full|screen|min|max|fit)$/
      },
      {
        pattern: /^(min-w|min-h|max-w|max-h)-(0|none|xs|sm|md|lg|xl|2xl|3xl|4xl|5xl|6xl|7xl|full|min|max|fit|prose|screen)$/
      },
      
      // 字体
      {
        pattern: /^text-(xs|sm|base|lg|xl|2xl|3xl|4xl|5xl|6xl|7xl|8xl|9xl)$/
      },
      {
        pattern: /^font-(thin|extralight|light|normal|medium|semibold|bold|extrabold|black)$/
      },
      {
        pattern: /^(italic|not-italic|text-left|text-center|text-right|text-justify|underline|overline|line-through|no-underline|uppercase|lowercase|capitalize|normal-case)$/
      },
      {
        pattern: /^leading-(3|4|5|6|7|8|9|10|none|tight|snug|normal|relaxed|loose)$/
      },
      
      // 边框
      {
        pattern: /^border(-t|-r|-b|-l)?(-0|-2|-4|-8)?$/
      },
      {
        pattern: /^border-(solid|dashed|dotted|double|hidden|none)$/
      },
      {
        pattern: /^rounded(-none|-sm|-md|-lg|-xl|-2xl|-3xl|-full)?$/
      },
      {
        pattern: /^rounded-(t|r|b|l|tl|tr|br|bl)(-none|-sm|-md|-lg|-xl|-2xl|-3xl|-full)?$/
      },
      
      // 阴影
      {
        pattern: /^(shadow|drop-shadow)(-sm|-md|-lg|-xl|-2xl|-inner|-none)?$/
      },
      
      // 透明度
      {
        pattern: /^opacity-(0|5|10|20|25|30|40|50|60|70|75|80|90|95|100)$/
      },
      
      // 过渡和变换
      {
        pattern: /^transition(-none|-all|-colors|-opacity|-shadow|-transform)?$/
      },
      {
        pattern: /^duration-(75|100|150|200|300|500|700|1000)$/
      },
      {
        pattern: /^ease-(linear|in|out|in-out)$/
      },
      {
        pattern: /^(transform|transform-none)$/
      },
      {
        pattern: /^scale-(0|50|75|90|95|100|105|110|125|150)$/
      },
      {
        pattern: /^rotate-(0|1|2|3|6|12|45|90|180)$/
      },
      {
        pattern: /^translate-(x|y)-(0|1|2|3|4|5|6|7|8|9|10|11|12|14|16|20|24|28|32|36|40|44|48|52|56|60|64|72|80|96|1\/2|1\/3|2\/3|1\/4|2\/4|3\/4|full)$/
      },
      
      // 其他常用
      {
        pattern: /^cursor-(auto|default|pointer|wait|text|move|help|not-allowed|none|context-menu|progress|cell|crosshair|vertical-text|alias|copy)$/
      },
      {
        pattern: /^select-(none|text|all|auto)$/
      },
      {
        pattern: /^resize(-none|-y|-x)?$/
      },
      {
        pattern: /^overflow(-x|-y)?-(auto|hidden|clip|visible|scroll)$/
      },
      {
        pattern: /^whitespace-(normal|nowrap|pre|pre-line|pre-wrap)$/
      },
      {
        pattern: /^break-(normal|words|all|keep)$/
      },
      {
        pattern: /^z-(0|10|20|30|40|50|auto)$/
      },
      
      // 响应式前缀
      {
        pattern: /^(sm|md|lg|xl|2xl):/
      },
      
      // 状态变体
      {
        pattern: /^(hover|focus|active|disabled|visited|first|last|odd|even|first-child|last-child):/
      },
      
      // 暗色模式
      {
        pattern: /^dark:/
      }
    ],
    
    darkMode: 'class',
    
    theme: {
      extend: {
        // 扩展Element Plus颜色变量
        colors: {
          'el-primary': 'var(--el-color-primary)',
          'el-success': 'var(--el-color-success)',
          'el-warning': 'var(--el-color-warning)',
          'el-danger': 'var(--el-color-danger)',
          'el-info': 'var(--el-color-info)',
          'el-bg': 'var(--el-bg-color)',
          'el-text-primary': 'var(--el-text-color-primary)',
          'el-text-regular': 'var(--el-text-color-regular)',
          'el-border': 'var(--el-border-color)',
        }
      }
    },
    
    corePlugins: {
      // 禁用预设样式，避免与Element Plus冲突
      preflight: false
    }
  }
}

/**
 * 构建完整的TailwindCSS文件
 */
async function buildFullTailwindCSS() {
  console.log('🎨 开始构建完整TailwindCSS文件...\n')
  
  try {
    // 确保目录存在
    const tailwindDir = path.join(libsDir, 'tailwind')
    if (!fs.existsSync(tailwindDir)) {
      fs.mkdirSync(tailwindDir, { recursive: true })
    }
    
    // 创建输入CSS
    const inputCSS = createComprehensiveInput()
    
    // 创建TailwindCSS配置
    const tailwindConfig = createFullTailwindConfig()
    
    console.log('⚙️ 处理TailwindCSS...')
    
    // 使用PostCSS处理CSS
    const result = await postcss([
      tailwindcss(tailwindConfig),
      autoprefixer()
    ]).process(inputCSS, { 
      from: undefined,
      to: undefined 
    })
    
    // 写入完整CSS文件
    const outputPath = path.join(tailwindDir, 'tailwind-full.css')
    fs.writeFileSync(outputPath, result.css)
    
    // 计算文件大小
    const stats = fs.statSync(outputPath)
    const fileSizeInKB = Math.round(stats.size / 1024)
    
    console.log('✅ TailwindCSS构建完成！')
    console.log(`📁 输出文件: ${outputPath}`)
    console.log(`📊 文件大小: ${fileSizeInKB} KB`)
    console.log(`🎯 包含所有TailwindCSS类，支持AI生成的任意代码`)
    
    // 创建压缩版本
    console.log('\n🗜️ 创建压缩版本...')
    const cssnano = (await import('cssnano')).default
    
    const minifiedResult = await postcss([
      cssnano({
        preset: ['default', {
          discardComments: { removeAll: true },
          normalizeWhitespace: true,
          minifyFontValues: true,
          minifySelectors: true
        }]
      })
    ]).process(result.css, { from: undefined })
    
    const minifiedPath = path.join(tailwindDir, 'tailwind-full.min.css')
    fs.writeFileSync(minifiedPath, minifiedResult.css)
    
    const minifiedStats = fs.statSync(minifiedPath)
    const minifiedSizeInKB = Math.round(minifiedStats.size / 1024)
    
    console.log(`✅ 压缩版本创建完成: ${minifiedSizeInKB} KB`)
    console.log(`📉 压缩率: ${Math.round((1 - minifiedStats.size / stats.size) * 100)}%`)
    
  } catch (error) {
    console.error('❌ TailwindCSS构建失败:', error.message)
    throw error
  }
}

// 运行构建
if (import.meta.url === `file://${process.argv[1]}`) {
  buildFullTailwindCSS()
}

export { buildFullTailwindCSS }