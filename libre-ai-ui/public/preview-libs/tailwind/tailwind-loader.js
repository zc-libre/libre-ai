/**
 * TailwindCSS离线加载器
 * 加载TailwindCSS Play版本并应用自定义配置
 */

/**
 * 加载TailwindCSS到预览环境
 * @param {HTMLIFrameElement} iframe 预览iframe
 * @returns {Promise<void>}
 */
export async function loadTailwindCSS(iframe) {
  return new Promise((resolve, reject) => {
    const iframeDoc = iframe.contentDocument || iframe.contentWindow.document
    
    // 1. 加载TailwindCSS Play脚本
    const script = iframeDoc.createElement('script')
    script.src = '/src/assets/preview-libs/tailwind/tailwind-play.js'
    script.onload = () => {
      // 2. 加载自定义配置
      loadTailwindConfig(iframeDoc)
        .then(resolve)
        .catch(reject)
    }
    script.onerror = () => reject(new Error('TailwindCSS加载失败'))
    
    iframeDoc.head.appendChild(script)
  })
}

/**
 * 加载TailwindCSS配置
 * @param {Document} doc iframe文档对象
 * @returns {Promise<void>}
 */
async function loadTailwindConfig(doc) {
  return new Promise((resolve, reject) => {
    // 加载配置脚本
    const configScript = doc.createElement('script')
    configScript.src = '/src/assets/preview-libs/tailwind/tailwind-config.js'
    configScript.onload = () => {
      // 应用配置
      if (doc.defaultView.tailwind && doc.defaultView.tailwindConfig) {
        doc.defaultView.tailwind.config = doc.defaultView.tailwindConfig
        console.log('✓ TailwindCSS配置已应用')
      }
      resolve()
    }
    configScript.onerror = () => reject(new Error('TailwindCSS配置加载失败'))
    
    doc.head.appendChild(configScript)
  })
}

/**
 * 为iframe添加TailwindCSS支持
 * @param {string} htmlContent HTML内容
 * @returns {string} 包含TailwindCSS的完整HTML
 */
export function wrapWithTailwind(htmlContent) {
  return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Vue组件预览</title>
  
  <!-- TailwindCSS Play CDN -->
  <script src="/src/assets/preview-libs/tailwind/tailwind-play.js"></script>
  
  <!-- TailwindCSS配置 -->
  <script src="/src/assets/preview-libs/tailwind/tailwind-config.js"></script>
  
  <!-- 应用TailwindCSS配置 -->
  <script>
    if (window.tailwind && window.tailwindConfig) {
      window.tailwind.config = window.tailwindConfig
    }
  </script>
  
  <style>
    body {
      margin: 0;
      padding: 0;
      font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background-color: #f5f7fa;
    }
    
    /* 暗色模式支持 */
    .dark body {
      background-color: #1a1a1a;
      color: #ffffff;
    }
  </style>
</head>
<body>
  ${htmlContent}
</body>
</html>`
}

/**
 * 获取TailwindCSS类的智能提示
 * @returns {string[]} 常用类名数组
 */
export function getTailwindSuggestions() {
  return [
    // 布局
    'flex', 'grid', 'block', 'inline-block', 'hidden',
    'container', 'mx-auto', 'w-full', 'h-full',
    
    // 间距
    'p-4', 'p-6', 'p-8', 'm-4', 'm-6', 'm-8',
    'px-4', 'py-2', 'mx-2', 'my-4',
    
    // 颜色
    'bg-white', 'bg-gray-100', 'bg-blue-500', 'bg-primary',
    'text-gray-800', 'text-white', 'text-primary',
    'border-gray-200', 'border-primary',
    
    // 尺寸
    'w-64', 'w-1/2', 'w-full', 'h-64', 'h-screen',
    'max-w-md', 'max-w-lg', 'max-w-xl',
    
    // 圆角和阴影
    'rounded', 'rounded-lg', 'rounded-full',
    'shadow', 'shadow-md', 'shadow-lg', 'shadow-card',
    
    // 响应式
    'sm:block', 'md:flex', 'lg:grid', 'xl:w-1/4',
    
    // 状态
    'hover:bg-blue-600', 'focus:outline-none', 'active:scale-95',
    
    // 动画
    'transition', 'duration-300', 'ease-in-out',
    'animate-fade-in', 'animate-slide-up'
  ]
}