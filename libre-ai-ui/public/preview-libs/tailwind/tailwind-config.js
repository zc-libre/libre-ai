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