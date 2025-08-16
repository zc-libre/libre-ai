import * as dayjs from 'dayjs'

/**
 * 格式化日期
 * @param date 日期值
 * @param format 格式化模板
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: any, format = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''
  
  try {
    return dayjs(date).format(format)
  } catch {
    return String(date)
  }
}

/**
 * 格式化数字
 * @param number 数字值
 * @param options 格式化选项
 * @returns 格式化后的数字字符串
 */
export function formatNumber(
  number: any, 
  options: {
    decimals?: number
    thousands?: string
    decimalPoint?: string
    prefix?: string
    suffix?: string
    percentage?: boolean
  } = {}
): string {
  if (number === null || number === undefined || number === '') return ''
  
  const num = Number(number)
  if (isNaN(num)) return String(number)
  
  const {
    decimals = 2,
    thousands = ',',
    decimalPoint = '.',
    prefix = '',
    suffix = '',
    percentage = false
  } = options
  
  let result = percentage ? (num * 100) : num
  
  // 保留小数位数
  if (decimals >= 0) {
    result = Number(result.toFixed(decimals))
  }
  
  // 转换为字符串并添加千位分隔符
  let parts = result.toString().split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, thousands)
  
  let formatted = parts.join(decimalPoint)
  
  // 添加前缀和后缀
  if (percentage) {
    formatted += '%'
  }
  
  return prefix + formatted + suffix
}