#!/usr/bin/env node

/**
 * Vue组件预览依赖库构建脚本
 * 下载并打包Vue、Element Plus、ECharts等前端依赖，支持完全离线预览
 */

import fs from 'fs'
import path from 'path'
import https from 'https'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.join(__dirname, '..')
const libsDir = path.join(projectRoot, 'src/assets/preview-libs')

// 依赖配置
const dependencies = {
  vue: {
    version: '3.5.13',
    files: [
      {
        url: 'https://unpkg.com/vue@3.5.13/dist/vue.esm-browser.js',
        output: 'vue/vue.esm-browser.js'
      },
      {
        url: 'https://unpkg.com/vue@3.5.13/dist/vue.global.js',
        output: 'vue/vue.global.js'
      }
    ]
  },
  
  'vue-compiler': {
    version: '3.5.13',
    files: [
      {
        url: 'https://unpkg.com/@vue/compiler-sfc@3.5.13/dist/compiler-sfc.esm-browser.js',
        output: 'vue/compiler-sfc.esm-browser.js'
      }
    ]
  },

  'element-plus': {
    version: '2.9.0',
    files: [
      {
        url: 'https://unpkg.com/element-plus@2.9.0/dist/index.full.js',
        output: 'element-plus/index.full.js'
      },
      {
        url: 'https://unpkg.com/element-plus@2.9.0/dist/index.css',
        output: 'element-plus/index.css'
      }
    ]
  },

  'element-plus-icons': {
    version: '2.3.1',
    files: [
      {
        url: 'https://unpkg.com/@element-plus/icons-vue@2.3.1/dist/index.js',
        output: 'element-plus/icons.js'
      }
    ]
  },

  echarts: {
    version: '5.5.1',
    files: [
      {
        url: 'https://unpkg.com/echarts@5.5.1/dist/echarts.min.js',
        output: 'echarts/echarts.min.js'
      }
    ]
  },

  'vue-echarts': {
    version: '6.7.3',
    files: [
      {
        url: 'https://unpkg.com/vue-echarts@6.7.3/dist/index.esm.min.js',
        output: 'echarts/vue-echarts.esm.min.js'
      }
    ]
  }
}

/**
 * 下载文件
 * @param {string} url 下载地址
 * @param {string} outputPath 输出路径
 * @returns {Promise<void>}
 */
function downloadFile(url, outputPath) {
  return new Promise((resolve, reject) => {
    const outputDir = path.dirname(outputPath)
    
    // 确保目录存在
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true })
    }

    console.log(`下载: ${url}`)
    
    const file = fs.createWriteStream(outputPath)
    
    https.get(url, (response) => {
      // 处理重定向
      if (response.statusCode === 301 || response.statusCode === 302) {
        file.close()
        fs.unlinkSync(outputPath)
        return downloadFile(response.headers.location, outputPath)
          .then(resolve)
          .catch(reject)
      }
      
      if (response.statusCode !== 200) {
        file.close()
        fs.unlinkSync(outputPath)
        return reject(new Error(`下载失败: ${response.statusCode} ${response.statusMessage}`))
      }
      
      response.pipe(file)
      
      file.on('finish', () => {
        file.close()
        console.log(`✓ 完成: ${path.basename(outputPath)}`)
        resolve()
      })
      
      file.on('error', (err) => {
        file.close()
        fs.unlinkSync(outputPath)
        reject(err)
      })
    }).on('error', (err) => {
      reject(err)
    })
  })
}

/**
 * 创建依赖映射文件
 */
function createImportMap() {
  const importMap = {
    imports: {
      'vue': './vue/vue.esm-browser.js',
      '@vue/compiler-sfc': './vue/compiler-sfc.esm-browser.js',
      'element-plus': './element-plus/index.full.js',
      '@element-plus/icons-vue': './element-plus/icons.js',
      'echarts': './echarts/echarts.min.js',
      'vue-echarts': './echarts/vue-echarts.esm.min.js'
    }
  }

  const mapPath = path.join(libsDir, 'import-map.json')
  fs.writeFileSync(mapPath, JSON.stringify(importMap, null, 2))
  console.log('✓ 创建导入映射文件')
}

/**
 * 创建版本信息文件
 */
function createVersionInfo() {
  const versions = {}
  
  for (const [name, config] of Object.entries(dependencies)) {
    versions[name] = config.version
  }
  
  const versionInfo = {
    buildTime: new Date().toISOString(),
    versions
  }
  
  const versionPath = path.join(libsDir, 'versions.json')
  fs.writeFileSync(versionPath, JSON.stringify(versionInfo, null, 2))
  console.log('✓ 创建版本信息文件')
}

/**
 * 创建依赖加载器
 */
function createLibLoader() {
  const loaderCode = `
/**
 * 离线依赖库加载器
 * 为Vue组件预览提供完全离线的依赖注入
 */

const LIBS_BASE_PATH = '/src/assets/preview-libs'

// 依赖配置
const libConfigs = {
  vue: {
    js: \`\${LIBS_BASE_PATH}/vue/vue.esm-browser.js\`,
    type: 'module'
  },
  'vue-compiler': {
    js: \`\${LIBS_BASE_PATH}/vue/compiler-sfc.esm-browser.js\`,
    type: 'module'
  },
  'element-plus': {
    js: \`\${LIBS_BASE_PATH}/element-plus/index.full.js\`,
    css: \`\${LIBS_BASE_PATH}/element-plus/index.css\`,
    type: 'global'
  },
  'element-plus-icons': {
    js: \`\${LIBS_BASE_PATH}/element-plus/icons.js\`,
    type: 'global'
  },
  echarts: {
    js: \`\${LIBS_BASE_PATH}/echarts/echarts.min.js\`,
    type: 'global'
  },
  'vue-echarts': {
    js: \`\${LIBS_BASE_PATH}/echarts/vue-echarts.esm.min.js\`,
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
      throw new Error(\`未知的依赖: \${dep}\`)
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
  
  // 加载JS
  await loadScript(config.js, config.type)
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
    link.onerror = () => reject(new Error(\`CSS加载失败: \${url}\`))
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
    script.onerror = () => reject(new Error(\`脚本加载失败: \${url}\`))
    document.head.appendChild(script)
  })
}

/**
 * 获取依赖版本信息
 * @returns {Promise<object>}
 */
export async function getVersionInfo() {
  try {
    const response = await fetch(\`\${LIBS_BASE_PATH}/versions.json\`)
    return await response.json()
  } catch (error) {
    console.warn('无法获取版本信息:', error)
    return {}
  }
}
`

  const loaderPath = path.join(libsDir, 'lib-loader.js')
  fs.writeFileSync(loaderPath, loaderCode.trim())
  console.log('✓ 创建依赖加载器')
}

/**
 * 主构建函数
 */
async function buildPreviewLibs() {
  console.log('🚀 开始构建Vue组件预览依赖库...\n')
  
  try {
    // 确保目录存在
    if (!fs.existsSync(libsDir)) {
      fs.mkdirSync(libsDir, { recursive: true })
    }
    
    // 下载所有依赖
    for (const [name, config] of Object.entries(dependencies)) {
      console.log(`\n📦 处理依赖: ${name} v${config.version}`)
      
      for (const file of config.files) {
        const outputPath = path.join(libsDir, file.output)
        await downloadFile(file.url, outputPath)
      }
    }
    
    // 创建辅助文件
    console.log('\n📝 创建配置文件...')
    createImportMap()
    createVersionInfo()
    createLibLoader()
    
    console.log('\n✅ 构建完成！')
    console.log(`📁 依赖库位置: ${libsDir}`)
    console.log('📊 依赖统计:')
    
    // 统计文件信息
    const stats = {}
    for (const [name, config] of Object.entries(dependencies)) {
      stats[name] = {
        version: config.version,
        files: config.files.length
      }
    }
    
    console.table(stats)
    
  } catch (error) {
    console.error('❌ 构建失败:', error.message)
    process.exit(1)
  }
}

// 运行构建
if (import.meta.url === `file://${process.argv[1]}`) {
  buildPreviewLibs()
}

export { buildPreviewLibs }