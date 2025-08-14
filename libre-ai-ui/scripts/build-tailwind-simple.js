#!/usr/bin/env node

/**
 * TailwindCSS简化构建脚本
 * 使用CDN的TailwindCSS Play版本，支持所有类并可以自定义配置
 */

import fs from 'fs'
import path from 'path'
import https from 'https'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.join(__dirname, '..')
const outputDir = path.join(projectRoot, 'src/assets/preview-libs/tailwind')

/**
 * 下载TailwindCSS Play CDN版本
 */
async function downloadTailwindPlay() {
  console.log('🎨 下载TailwindCSS Play版本...')
  
  // 确保输出目录存在
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true })
  }
  
  const playScriptUrl = 'https://cdn.tailwindcss.com/3.4.1'
  const outputPath = path.join(outputDir, 'tailwind-play.js')
  
  return new Promise((resolve, reject) => {
    console.log(`下载: ${playScriptUrl}`)
    
    const file = fs.createWriteStream(outputPath)
    
    https.get(playScriptUrl, (response) => {
      if (response.statusCode !== 200) {
        file.close()
        fs.unlinkSync(outputPath)
        return reject(new Error(`下载失败: ${response.statusCode}`))
      }
      
      response.pipe(file)
      
      file.on('finish', () => {
        file.close()
        console.log('✓ TailwindCSS Play版本下载完成')
        resolve()
      })
      
      file.on('error', (err) => {
        file.close()
        fs.unlinkSync(outputPath)
        reject(err)
      })
    }).on('error', reject)
  })
}

/**
 * 创建TailwindCSS配置文件
 */
function createTailwindConfig() {
  const configContent = `
/**
 * TailwindCSS预览环境配置
 * 与Element Plus主题集成，支持暗色模式
 */
window.tailwindConfig = {
  darkMode: 'class',
  theme: {
    extend: {
      // 扩展颜色匹配Element Plus
      colors: {
        // Element Plus主色
        'el-primary': '#409EFF',
        'el-success': '#67C23A', 
        'el-warning': '#E6A23C',
        'el-danger': '#F56C6C',
        'el-info': '#909399',
        
        // 扩展主色系
        primary: {
          50: '#ecf5ff',
          100: '#d9ecff', 
          200: '#b3d8ff',
          300: '#8cc5ff',
          400: '#66b1ff',
          500: '#409eff',
          600: '#337ecc',
          700: '#265f99',
          800: '#1a3f66',
          900: '#0d2033'
        },
        
        // 成功色系
        success: {
          50: '#f0f9e8',
          100: '#e1f3d1',
          200: '#c3e7a3',
          300: '#a4da75',
          400: '#86ce47',
          500: '#67c23a',
          600: '#529b2e',
          700: '#3e7423',
          800: '#294e18',
          900: '#15270c'
        },
        
        // 警告色系
        warning: {
          50: '#fdf6ec',
          100: '#fbecd9',
          200: '#f7d9b3',
          300: '#f3c68c',
          400: '#efb366',
          500: '#e6a23c',
          600: '#b8822f',
          700: '#8a6123',
          800: '#5c4118',
          900: '#2e200c'
        },
        
        // 危险色系  
        danger: {
          50: '#fef0f0',
          100: '#fde2e2',
          200: '#fbc4c4',
          300: '#f9a7a7',
          400: '#f78989',
          500: '#f56c6c',
          600: '#c45656',
          700: '#934141',
          800: '#622b2b',
          900: '#311616'
        }
      },
      
      // 字体家族
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system', 
          'BlinkMacSystemFont',
          '"Segoe UI"',
          'Roboto',
          '"Helvetica Neue"',
          'Arial',
          'sans-serif'
        ]
      },
      
      // 动画
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'bounce-in': 'bounceIn 0.6s ease-out'
      },
      
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' }
        },
        slideUp: {
          '0%': { 
            opacity: '0',
            transform: 'translateY(10px)' 
          },
          '100%': { 
            opacity: '1',
            transform: 'translateY(0)' 
          }
        },
        bounceIn: {
          '0%': { 
            opacity: '0',
            transform: 'scale(0.3)' 
          },
          '50%': { 
            opacity: '1',
            transform: 'scale(1.05)' 
          },
          '70%': { 
            transform: 'scale(0.9)' 
          },
          '100%': { 
            opacity: '1',
            transform: 'scale(1)' 
          }
        }
      },
      
      // 阴影
      boxShadow: {
        'card': '0 2px 4px 0 rgba(0, 0, 0, 0.12), 0 0 6px 0 rgba(0, 0, 0, 0.04)',
        'card-hover': '0 4px 8px 0 rgba(0, 0, 0, 0.12), 0 2px 4px 0 rgba(0, 0, 0, 0.08)',
        'dialog': '0 8px 32px rgba(0, 0, 0, 0.08), 0 4px 16px rgba(0, 0, 0, 0.04)'
      }
    }
  },
  
  plugins: [
    // 自定义工具类
    function({ addUtilities }) {
      const newUtilities = {
        // Element Plus兼容类
        '.el-card': {
          backgroundColor: 'var(--el-bg-color)',
          border: '1px solid var(--el-border-color)',
          borderRadius: '4px',
          boxShadow: '0 2px 4px 0 rgba(0, 0, 0, 0.12), 0 0 6px 0 rgba(0, 0, 0, 0.04)',
          color: 'var(--el-text-color-primary)',
          transition: 'all 0.3s ease'
        },
        
        '.el-button': {
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '8px 15px',
          fontSize: '14px',
          borderRadius: '4px',
          border: '1px solid var(--el-border-color)',
          backgroundColor: 'var(--el-bg-color)',
          color: 'var(--el-text-color-primary)',
          cursor: 'pointer',
          transition: 'all 0.3s ease'
        },
        
        // 仪表板专用类
        '.dashboard-card': {
          backgroundColor: 'white',
          borderRadius: '8px',
          padding: '24px',
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)',
          border: '1px solid rgba(0, 0, 0, 0.06)',
          transition: 'all 0.3s ease'
        },
        
        '.dashboard-header': {
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '16px',
          paddingBottom: '12px',
          borderBottom: '1px solid rgba(0, 0, 0, 0.06)'
        },
        
        '.chart-container': {
          position: 'relative',
          width: '100%',
          height: '300px',
          backgroundColor: 'white',
          borderRadius: '8px',
          padding: '16px'
        }
      }
      
      addUtilities(newUtilities)
    }
  ]
}
`

  const configPath = path.join(outputDir, 'tailwind-config.js')
  fs.writeFileSync(configPath, configContent.trim())
  console.log('✓ 创建TailwindCSS配置文件')
}

/**
 * 创建TailwindCSS加载器
 */
function createTailwindLoader() {
  const loaderContent = `
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
  return \`<!DOCTYPE html>
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
  \${htmlContent}
</body>
</html>\`
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
`

  const loaderPath = path.join(outputDir, 'tailwind-loader.js')
  fs.writeFileSync(loaderPath, loaderContent.trim())
  console.log('✓ 创建TailwindCSS加载器')
}

/**
 * 主构建函数
 */
async function buildTailwindSimple() {
  console.log('🎨 开始构建TailwindCSS离线版本（简化版）...\n')
  
  try {
    // 下载TailwindCSS Play版本
    await downloadTailwindPlay()
    
    // 创建配置和加载器
    createTailwindConfig()
    createTailwindLoader()
    
    // 获取文件大小
    const playScriptPath = path.join(outputDir, 'tailwind-play.js')
    const playSize = fs.statSync(playScriptPath).size
    
    console.log('\n✅ TailwindCSS离线版本构建完成！')
    console.log(`📁 输出目录: ${outputDir}`)
    console.log('📊 文件信息:')
    console.log(`   TailwindCSS Play: ${(playSize / 1024).toFixed(2)} KB`)
    console.log('   配置文件: tailwind-config.js')
    console.log('   加载器: tailwind-loader.js')
    console.log('\n🔧 特性:')
    console.log('   ✓ 支持所有TailwindCSS类')
    console.log('   ✓ 实时JIT编译')
    console.log('   ✓ Element Plus主题集成')
    console.log('   ✓ 暗色模式支持')
    console.log('   ✓ 自定义动画和工具类')
    
  } catch (error) {
    console.error('❌ 构建失败:', error.message)
    process.exit(1)
  }
}

// 运行构建
if (import.meta.url === `file://${process.argv[1]}`) {
  buildTailwindSimple()
}

export { buildTailwindSimple }