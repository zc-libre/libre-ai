import { ref, reactive, computed } from 'vue'
import type { DictConfig } from '../crud-types'

// 字典数据类型
export interface DictItem {
  label: string
  value: any
  disabled?: boolean
  children?: DictItem[]
  [key: string]: any
}

// 字典缓存项
interface DictCacheItem {
  data: DictItem[]
  timestamp: number
  loading: boolean
  error?: string
}

// 字典管理器类
class DictManagerClass {
  private cache = reactive<Map<string, DictCacheItem>>(new Map())
  private cacheTimeout = 5 * 60 * 1000 // 5分钟缓存

  /**
   * 获取字典数据
   */
  async getDict(key: string, config: DictConfig): Promise<DictItem[]> {
    // 检查缓存
    if (config.cache !== false && this.cache.has(key)) {
      const cacheItem = this.cache.get(key)!
      
      // 如果正在加载，返回空数组
      if (cacheItem.loading) {
        return []
      }
      
      // 检查缓存是否过期
      if (Date.now() - cacheItem.timestamp < this.cacheTimeout) {
        return cacheItem.data
      }
    }

    // 设置加载状态
    this.cache.set(key, {
      data: [],
      timestamp: Date.now(),
      loading: true
    })

    try {
      let data: DictItem[] = []

      if (config.data) {
        // 静态数据
        data = this.transformData(config.data, config)
      } else if (config.url) {
        // 远程数据
        const response = await fetch(config.url)
        const result = await response.json()
        
        if (config.transform) {
          data = config.transform(result)
        } else {
          data = this.transformData(result, config)
        }
      }

      // 更新缓存
      this.cache.set(key, {
        data,
        timestamp: Date.now(),
        loading: false
      })

      return data
    } catch (error) {
      console.error(`加载字典 ${key} 失败:`, error)
      
      // 设置错误状态
      this.cache.set(key, {
        data: [],
        timestamp: Date.now(),
        loading: false,
        error: error instanceof Error ? error.message : '加载失败'
      })

      return []
    }
  }

  /**
   * 数据转换
   */
  private transformData(data: any[], config: DictConfig): DictItem[] {
    const labelField = config.label || 'label'
    const valueField = config.value || 'value'

    return data.map(item => {
      if (typeof item === 'object' && item !== null) {
        return {
          label: item[labelField] || item.label || item.name || String(item.value),
          value: item[valueField] !== undefined ? item[valueField] : item.value,
          ...item
        }
      }
      
      return {
        label: String(item),
        value: item
      }
    })
  }

  /**
   * 根据值获取标签
   */
  getLabel(key: string, value: any): string {
    if (!this.cache.has(key)) {
      return String(value)
    }

    const cacheItem = this.cache.get(key)!
    if (cacheItem.loading) {
      return String(value)
    }

    const item = cacheItem.data.find(item => item.value === value)
    return item ? item.label : String(value)
  }

  /**
   * 根据值获取完整的字典项
   */
  getItem(key: string, value: any): DictItem | undefined {
    if (!this.cache.has(key)) {
      return undefined
    }

    const cacheItem = this.cache.get(key)!
    if (cacheItem.loading) {
      return undefined
    }

    return cacheItem.data.find(item => item.value === value)
  }

  /**
   * 获取字典的所有选项
   */
  getOptions(key: string): DictItem[] {
    if (!this.cache.has(key)) {
      return []
    }

    const cacheItem = this.cache.get(key)!
    return cacheItem.loading ? [] : cacheItem.data
  }

  /**
   * 刷新字典缓存
   */
  async refresh(key: string, config: DictConfig): Promise<DictItem[]> {
    this.cache.delete(key)
    return this.getDict(key, config)
  }

  /**
   * 清除指定字典缓存
   */
  clear(key: string): void {
    this.cache.delete(key)
  }

  /**
   * 清除所有缓存
   */
  clearAll(): void {
    this.cache.clear()
  }

  /**
   * 预加载字典
   */
  async preload(dictConfigs: Record<string, DictConfig>): Promise<void> {
    const promises = Object.entries(dictConfigs).map(([key, config]) =>
      this.getDict(key, config)
    )
    await Promise.all(promises)
  }

  /**
   * 检查字典是否正在加载
   */
  isLoading(key: string): boolean {
    const cacheItem = this.cache.get(key)
    return cacheItem ? cacheItem.loading : false
  }

  /**
   * 获取字典错误信息
   */
  getError(key: string): string | undefined {
    const cacheItem = this.cache.get(key)
    return cacheItem?.error
  }

  /**
   * 注册全局字典
   */
  registerGlobal(dictConfigs: Record<string, DictConfig>): void {
    // 预加载全局字典
    this.preload(dictConfigs).catch(error => {
      console.warn('预加载全局字典失败:', error)
    })
  }
}

// 创建全局字典管理器实例
export const DictManager = new DictManagerClass()

// Composition API
export function useDict(key: string, config: DictConfig) {
  const data = ref<DictItem[]>([])
  const loading = ref(false)
  const error = ref<string>()

  const loadDict = async () => {
    loading.value = true
    error.value = undefined
    
    try {
      data.value = await DictManager.getDict(key, config)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载失败'
    } finally {
      loading.value = false
    }
  }

  const refresh = async () => {
    return DictManager.refresh(key, config).then(result => {
      data.value = result
      return result
    })
  }

  const getLabel = (value: any) => {
    return DictManager.getLabel(key, value)
  }

  const getItem = (value: any) => {
    return DictManager.getItem(key, value)
  }

  // 自动加载
  loadDict()

  return {
    data: computed(() => data.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    refresh,
    getLabel,
    getItem,
    reload: loadDict
  }
}

// 多字典管理
export function useDicts(dictConfigs: Record<string, DictConfig>) {
  const dicts = reactive<Record<string, any>>({})

  Object.entries(dictConfigs).forEach(([key, config]) => {
    dicts[key] = useDict(key, config)
  })

  const refresh = async () => {
    const promises = Object.entries(dictConfigs).map(([key, config]) =>
      DictManager.refresh(key, config)
    )
    await Promise.all(promises)
  }

  const isAllLoaded = computed(() => {
    return Object.values(dicts).every((dict: any) => !dict.loading.value)
  })

  return {
    dicts,
    refresh,
    isAllLoaded
  }
}