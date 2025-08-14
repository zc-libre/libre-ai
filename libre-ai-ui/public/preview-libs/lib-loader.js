/**
 * 离线依赖库加载器
 * 为Vue组件预览提供完全离线的依赖注入
 */

const LIBS_BASE_PATH = '/src/assets/preview-libs'

// 依赖配置
const libConfigs = {
  vue: {
    js: `${LIBS_BASE_PATH}/vue/vue.esm-browser.js`,
    type: 'module'
  },
  'vue-compiler': {
    js: `${LIBS_BASE_PATH}/vue/compiler-sfc.esm-browser.js`,
    type: 'module'
  },
  'element-plus': {
    js: `${LIBS_BASE_PATH}/element-plus/index.full.js`,
    css: `${LIBS_BASE_PATH}/element-plus/index.css`,
    type: 'global'
  },
  'tailwindcss': {
    css: `${LIBS_BASE_PATH}/tailwind/tailwind-full.min.css`,
    type: 'css-only'
  },
  'element-plus-icons': {
    js: `${LIBS_BASE_PATH}/element-plus/icons.js`,
    type: 'global'
  },
  echarts: {
    js: `${LIBS_BASE_PATH}/echarts/echarts.min.js`,
    type: 'global'
  },
  'vue-echarts': {
    js: `${LIBS_BASE_PATH}/echarts/vue-echarts.esm.min.js`,
    type: 'module'
  }
}

/**
 * 加载依赖库
 * @param {string[]} deps 依赖名称数组
 * @returns {Promise<void>}
 */
export async function loadDependencies(deps) {
  const promises = deps.map(dep => {
    const config = libConfigs[dep]
    if (!config) {
      throw new Error(`未知的依赖: ${dep}`)
    }
    
    return loadLibrary(dep, config)
  })
  
  await Promise.all(promises)
}

/**
 * 加载单个库
 * @param {string} name 库名称
 * @param {object} config 库配置
 * @returns {Promise<void>}
 */
async function loadLibrary(name, config) {
  // 加载CSS
  if (config.css) {
    await loadCSS(config.css)
  }
  
  // 加载JS (如果有)
  if (config.js) {
    await loadScript(config.js, config.type)
  }
}

/**
 * 加载CSS文件
 * @param {string} url CSS文件路径
 * @returns {Promise<void>}
 */
function loadCSS(url) {
  return new Promise((resolve, reject) => {
    const link = document.createElement('link')
    link.rel = 'stylesheet'
    link.href = url
    link.onload = resolve
    link.onerror = () => reject(new Error(`CSS加载失败: ${url}`))
    document.head.appendChild(link)
  })
}

/**
 * 加载JS文件
 * @param {string} url JS文件路径
 * @param {string} type 加载类型 ('module' | 'global')
 * @returns {Promise<void>}
 */
function loadScript(url, type) {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script')
    
    if (type === 'module') {
      script.type = 'module'
    }
    
    script.src = url
    script.onload = resolve
    script.onerror = () => reject(new Error(`脚本加载失败: ${url}`))
    document.head.appendChild(script)
  })
}

/**
 * 获取依赖版本信息
 * @returns {Promise<object>}
 */
export async function getVersionInfo() {
  try {
    const response = await fetch(`${LIBS_BASE_PATH}/versions.json`)
    return await response.json()
  } catch (error) {
    console.warn('无法获取版本信息:', error)
    return {}
  }
}