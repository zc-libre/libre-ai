#!/usr/bin/env node

/**
 * TailwindCSS离线构建脚本
 * 生成包含所有TailwindCSS类的完整CSS文件，支持AI生成的不确定代码
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'
import tailwindcss from 'tailwindcss'
import postcss from 'postcss'
import autoprefixer from 'autoprefixer'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.join(__dirname, '..')
const outputDir = path.join(projectRoot, 'src/assets/preview-libs/tailwind')

/**
 * 生成包含所有可能类的safelist
 * 覆盖AI可能生成的所有TailwindCSS类组合
 */
function generateCompleteSafelist() {
  const safelist = []
  
  // 间距类 (0-96, auto)
  const spacingValues = ['0', '0.5', '1', '1.5', '2', '2.5', '3', '3.5', '4', '5', '6', '7', '8', '9', '10', '11', '12', '14', '16', '20', '24', '28', '32', '36', '40', '44', '48', '52', '56', '60', '64', '72', '80', '96', 'auto', 'px']
  const spacingProperties = ['p', 'pt', 'pr', 'pb', 'pl', 'px', 'py', 'm', 'mt', 'mr', 'mb', 'ml', 'mx', 'my', 'space-x', 'space-y']
  
  spacingProperties.forEach(prop => {
    spacingValues.forEach(value => {
      safelist.push(`${prop}-${value}`)
      if (prop.startsWith('space-')) {
        safelist.push(`-${prop}-${value}`) // 负值
      } else if (['m', 'mt', 'mr', 'mb', 'ml', 'mx', 'my'].includes(prop)) {
        safelist.push(`-${prop}-${value}`) // 负值margin
      }
    })
  })
  
  // 尺寸类 (width, height)
  const sizeValues = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '14', '16', '20', '24', '28', '32', '36', '40', '44', '48', '52', '56', '60', '64', '72', '80', '96', 'auto', 'full', 'screen', 'min', 'max', 'fit']
  const sizeProperties = ['w', 'h', 'min-w', 'min-h', 'max-w', 'max-h']
  
  sizeProperties.forEach(prop => {
    sizeValues.forEach(value => {
      safelist.push(`${prop}-${value}`)
    })
  })
  
  // 响应式前缀
  const breakpoints = ['sm', 'md', 'lg', 'xl', '2xl']
  
  // 颜色类 (包含所有TailwindCSS默认颜色)
  const colors = ['slate', 'gray', 'zinc', 'neutral', 'stone', 'red', 'orange', 'amber', 'yellow', 'lime', 'green', 'emerald', 'teal', 'cyan', 'sky', 'blue', 'indigo', 'violet', 'purple', 'fuchsia', 'pink', 'rose']
  const colorShades = ['50', '100', '200', '300', '400', '500', '600', '700', '800', '900', '950']
  const colorProperties = ['bg', 'text', 'border', 'ring', 'shadow', 'from', 'via', 'to']
  
  colorProperties.forEach(prop => {
    colors.forEach(color => {
      colorShades.forEach(shade => {
        const className = `${prop}-${color}-${shade}`
        safelist.push(className)
        
        // 添加响应式版本
        breakpoints.forEach(bp => {
          safelist.push(`${bp}:${className}`)
        })
      })
    })
    
    // 特殊颜色值
    const specialColors = ['transparent', 'current', 'white', 'black', 'inherit']
    specialColors.forEach(color => {
      safelist.push(`${prop}-${color}`)
    })
  })
  
  // Flexbox和Grid类
  const flexClasses = [
    'flex', 'inline-flex', 'flex-row', 'flex-row-reverse', 'flex-col', 'flex-col-reverse',
    'flex-wrap', 'flex-wrap-reverse', 'flex-nowrap',
    'flex-1', 'flex-auto', 'flex-initial', 'flex-none',
    'grow', 'grow-0', 'shrink', 'shrink-0',
    'items-start', 'items-end', 'items-center', 'items-baseline', 'items-stretch',
    'justify-start', 'justify-end', 'justify-center', 'justify-between', 'justify-around', 'justify-evenly',
    'content-center', 'content-start', 'content-end', 'content-between', 'content-around', 'content-evenly'
  ]
  
  const gridClasses = [
    'grid', 'inline-grid', 'grid-cols-1', 'grid-cols-2', 'grid-cols-3', 'grid-cols-4', 'grid-cols-5', 'grid-cols-6', 
    'grid-cols-7', 'grid-cols-8', 'grid-cols-9', 'grid-cols-10', 'grid-cols-11', 'grid-cols-12', 'grid-cols-none',
    'col-auto', 'col-span-1', 'col-span-2', 'col-span-3', 'col-span-4', 'col-span-5', 'col-span-6',
    'col-span-7', 'col-span-8', 'col-span-9', 'col-span-10', 'col-span-11', 'col-span-12', 'col-span-full',
    'grid-rows-1', 'grid-rows-2', 'grid-rows-3', 'grid-rows-4', 'grid-rows-5', 'grid-rows-6', 'grid-rows-none',
    'row-auto', 'row-span-1', 'row-span-2', 'row-span-3', 'row-span-4', 'row-span-5', 'row-span-6', 'row-span-full',
    'gap-0', 'gap-1', 'gap-2', 'gap-3', 'gap-4', 'gap-5', 'gap-6', 'gap-8', 'gap-10', 'gap-12', 'gap-16', 'gap-20', 'gap-24'
  ]
  
  // 布局类
  const layoutClasses = [
    'block', 'inline-block', 'inline', 'hidden', 'table', 'table-cell', 'table-row',
    'absolute', 'relative', 'fixed', 'sticky', 'static',
    'top-0', 'right-0', 'bottom-0', 'left-0', 'inset-0',
    'z-0', 'z-10', 'z-20', 'z-30', 'z-40', 'z-50', 'z-auto',
    'float-left', 'float-right', 'float-none', 'clear-left', 'clear-right', 'clear-both', 'clear-none'
  ]
  
  // 字体和文本类
  const textClasses = [
    'text-xs', 'text-sm', 'text-base', 'text-lg', 'text-xl', 'text-2xl', 'text-3xl', 'text-4xl', 'text-5xl', 'text-6xl',
    'font-thin', 'font-extralight', 'font-light', 'font-normal', 'font-medium', 'font-semibold', 'font-bold', 'font-extrabold', 'font-black',
    'text-left', 'text-center', 'text-right', 'text-justify',
    'leading-3', 'leading-4', 'leading-5', 'leading-6', 'leading-7', 'leading-8', 'leading-9', 'leading-10',
    'leading-none', 'leading-tight', 'leading-snug', 'leading-normal', 'leading-relaxed', 'leading-loose',
    'tracking-tighter', 'tracking-tight', 'tracking-normal', 'tracking-wide', 'tracking-wider', 'tracking-widest',
    'uppercase', 'lowercase', 'capitalize', 'normal-case',
    'underline', 'overline', 'line-through', 'no-underline',
    'truncate', 'text-ellipsis', 'text-clip', 'break-normal', 'break-words', 'break-all'
  ]
  
  // 边框和圆角类
  const borderClasses = [
    'border', 'border-0', 'border-2', 'border-4', 'border-8',
    'border-t', 'border-r', 'border-b', 'border-l',
    'border-solid', 'border-dashed', 'border-dotted', 'border-double', 'border-none',
    'rounded', 'rounded-none', 'rounded-sm', 'rounded-md', 'rounded-lg', 'rounded-xl', 'rounded-2xl', 'rounded-3xl', 'rounded-full',
    'rounded-t', 'rounded-r', 'rounded-b', 'rounded-l',
    'rounded-tl', 'rounded-tr', 'rounded-br', 'rounded-bl'
  ]
  
  // 阴影和效果类
  const effectClasses = [
    'shadow', 'shadow-sm', 'shadow-md', 'shadow-lg', 'shadow-xl', 'shadow-2xl', 'shadow-inner', 'shadow-none',
    'opacity-0', 'opacity-25', 'opacity-50', 'opacity-75', 'opacity-100',
    'blur', 'blur-sm', 'blur-md', 'blur-lg', 'blur-xl', 'blur-2xl', 'blur-3xl', 'blur-none',
    'brightness-0', 'brightness-50', 'brightness-75', 'brightness-90', 'brightness-95', 'brightness-100', 'brightness-105', 'brightness-110', 'brightness-125', 'brightness-150', 'brightness-200',
    'contrast-0', 'contrast-50', 'contrast-75', 'contrast-100', 'contrast-125', 'contrast-150', 'contrast-200'
  ]
  
  // 变换类
  const transformClasses = [
    'transform', 'transform-none',
    'scale-0', 'scale-50', 'scale-75', 'scale-90', 'scale-95', 'scale-100', 'scale-105', 'scale-110', 'scale-125', 'scale-150',
    'rotate-0', 'rotate-1', 'rotate-2', 'rotate-3', 'rotate-6', 'rotate-12', 'rotate-45', 'rotate-90', 'rotate-180',
    '-rotate-180', '-rotate-90', '-rotate-45', '-rotate-12', '-rotate-6', '-rotate-3', '-rotate-2', '-rotate-1',
    'translate-x-0', 'translate-x-1', 'translate-x-2', 'translate-x-4', 'translate-x-8', 'translate-x-16', 'translate-x-32',
    'translate-y-0', 'translate-y-1', 'translate-y-2', 'translate-y-4', 'translate-y-8', 'translate-y-16', 'translate-y-32',
    'skew-x-0', 'skew-x-1', 'skew-x-2', 'skew-x-3', 'skew-x-6', 'skew-x-12',
    'skew-y-0', 'skew-y-1', 'skew-y-2', 'skew-y-3', 'skew-y-6', 'skew-y-12'
  ]
  
  // 过渡和动画类
  const transitionClasses = [
    'transition', 'transition-none', 'transition-all', 'transition-colors', 'transition-opacity', 'transition-shadow', 'transition-transform',
    'duration-75', 'duration-100', 'duration-150', 'duration-200', 'duration-300', 'duration-500', 'duration-700', 'duration-1000',
    'ease-linear', 'ease-in', 'ease-out', 'ease-in-out',
    'delay-75', 'delay-100', 'delay-150', 'delay-200', 'delay-300', 'delay-500', 'delay-700', 'delay-1000',
    'animate-none', 'animate-spin', 'animate-ping', 'animate-pulse', 'animate-bounce'
  ]
  
  // 状态变体类
  const stateVariants = ['hover', 'focus', 'active', 'visited', 'disabled', 'checked', 'first', 'last', 'odd', 'even']
  
  // 组合所有基础类
  const baseClasses = [
    ...flexClasses,
    ...gridClasses,
    ...layoutClasses,
    ...textClasses,
    ...borderClasses,
    ...effectClasses,
    ...transformClasses,
    ...transitionClasses
  ]
  
  safelist.push(...baseClasses)
  
  // 为基础类添加响应式和状态变体
  baseClasses.forEach(baseClass => {
    // 响应式变体
    breakpoints.forEach(bp => {
      safelist.push(`${bp}:${baseClass}`)
    })
    
    // 状态变体
    stateVariants.forEach(state => {
      safelist.push(`${state}:${baseClass}`)
      
      // 响应式 + 状态变体组合
      breakpoints.forEach(bp => {
        safelist.push(`${bp}:${state}:${baseClass}`)
      })
    })
  })
  
  // 暗色模式变体
  const darkModeClasses = []
  baseClasses.forEach(baseClass => {
    darkModeClasses.push(`dark:${baseClass}`)
    
    // 暗色模式 + 响应式
    breakpoints.forEach(bp => {
      darkModeClasses.push(`${bp}:dark:${baseClass}`)
    })
    
    // 暗色模式 + 状态变体
    stateVariants.forEach(state => {
      darkModeClasses.push(`dark:${state}:${baseClass}`)
    })
  })
  
  safelist.push(...darkModeClasses)
  
  return [...new Set(safelist)] // 去重
}

/**
 * TailwindCSS配置
 */
const tailwindConfig = {
  content: [], // 空content，使用safelist
  darkMode: 'class',
  corePlugins: {
    preflight: false // 禁用预设样式重置
  },
  theme: {
    extend: {
      // 扩展颜色以匹配Element Plus
      colors: {
        primary: {
          50: '#e6f7ff',
          100: '#bae7ff',
          200: '#91d5ff',
          300: '#69c0ff',
          400: '#40a9ff',
          500: '#1890ff',
          600: '#096dd9',
          700: '#0050b3',
          800: '#003a8c',
          900: '#002766'
        }
      }
    }
  },
  safelist: [
    // 包含所有可能的类
    {
      pattern: /.+/,
      variants: ['sm', 'md', 'lg', 'xl', '2xl', 'hover', 'focus', 'active', 'dark']
    }
  ]
}

/**
 * 构建完整的TailwindCSS
 */
async function buildTailwindOffline() {
  console.log('🎨 开始构建TailwindCSS离线版本...\n')
  
  try {
    // 确保输出目录存在
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true })
    }
    
    // 生成safelist
    console.log('📝 生成完整的类列表...')
    const safelist = generateCompleteSafelist()
    console.log(`✓ 生成了 ${safelist.length} 个TailwindCSS类`)
    
    // 创建输入CSS
    const inputCSS = `
@tailwind base;
@tailwind components;
@tailwind utilities;

/* 确保所有类都被包含的虚拟选择器 */
.safelist {
  ${safelist.map(cls => `@apply ${cls.replace(':', '\\\\:')};`).join('\n  ')}
}
`
    
    // 更新配置的safelist
    tailwindConfig.safelist = safelist
    
    console.log('🔧 编译TailwindCSS...')
    
    // 使用PostCSS编译
    const result = await postcss([
      tailwindcss(tailwindConfig),
      autoprefixer()
    ]).process(inputCSS, { from: undefined })
    
    // 输出文件
    const outputPath = path.join(outputDir, 'tailwind-complete.css')
    fs.writeFileSync(outputPath, result.css)
    
    // 创建压缩版本
    const cssnano = (await import('cssnano')).default
    const minifiedResult = await postcss([
      cssnano({ preset: 'default' })
    ]).process(result.css, { from: undefined })
    
    const minOutputPath = path.join(outputDir, 'tailwind-complete.min.css')
    fs.writeFileSync(minOutputPath, minifiedResult.css)
    
    // 获取文件大小信息
    const fullSize = fs.statSync(outputPath).size
    const minSize = fs.statSync(minOutputPath).size
    
    console.log('\n✅ TailwindCSS离线版本构建完成！')
    console.log(`📁 输出目录: ${outputDir}`)
    console.log('📊 文件信息:')
    console.log(`   完整版: ${(fullSize / 1024 / 1024).toFixed(2)} MB`)
    console.log(`   压缩版: ${(minSize / 1024 / 1024).toFixed(2)} MB`)
    console.log(`   包含类数: ${safelist.length}`)
    
    // 创建类列表文件供调试使用
    const classListPath = path.join(outputDir, 'class-list.json')
    fs.writeFileSync(classListPath, JSON.stringify({
      totalClasses: safelist.length,
      buildTime: new Date().toISOString(),
      classes: safelist.sort()
    }, null, 2))
    
    console.log(`📋 类列表: ${classListPath}`)
    
  } catch (error) {
    console.error('❌ 构建失败:', error.message)
    process.exit(1)
  }
}

// 运行构建
if (import.meta.url === `file://${process.argv[1]}`) {
  buildTailwindOffline()
}

export { buildTailwindOffline }