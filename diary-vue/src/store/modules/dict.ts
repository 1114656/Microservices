import { defineStore } from 'pinia'
import { store } from '../index'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'

const { wsCache } = useCache('sessionStorage')

export interface DictValueType {
  value: any
  label: string
  colorType?: string
  cssClass?: string
}

export interface DictState {
  dictMap: Record<string, DictValueType[]>
  isSetDict: boolean
}

const slimDictMap: Record<string, DictValueType[]> = {
  common_status: [
    { value: 0, label: '开启', colorType: 'success' },
    { value: 1, label: '关闭', colorType: 'danger' }
  ],
  system_user_sex: [
    { value: 1, label: '男' },
    { value: 2, label: '女' },
    { value: 0, label: '未知' }
  ],
  system_menu_type: [
    { value: 1, label: '目录' },
    { value: 2, label: '菜单' },
    { value: 3, label: '按钮' }
  ],
  system_role_type: [
    { value: 1, label: '内置' },
    { value: 2, label: '自定义' }
  ]
}

export const useDictStore = defineStore('dict', {
  state: (): DictState => ({
    dictMap: {},
    isSetDict: false
  }),
  getters: {
    getDictMap(): Record<string, DictValueType[]> {
      const dictMap = wsCache.get(CACHE_KEY.DICT_CACHE)
      if (dictMap) {
        this.dictMap = dictMap
      }
      return this.dictMap
    },
    getIsSetDict(): boolean {
      return this.isSetDict
    }
  },
  actions: {
    async setDictMap() {
      this.dictMap = slimDictMap
      this.isSetDict = true
      wsCache.set(CACHE_KEY.DICT_CACHE, slimDictMap, { exp: 60 })
    },
    getDictByType(type: string) {
      if (!this.isSetDict) {
        this.setDictMap()
      }
      return this.dictMap[type] || []
    },
    async resetDict() {
      wsCache.delete(CACHE_KEY.DICT_CACHE)
      await this.setDictMap()
    }
  }
})

export const useDictStoreWithOut = () => {
  return useDictStore(store)
}
