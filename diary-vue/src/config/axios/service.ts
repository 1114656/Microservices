import axios, { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import qs from 'qs'
import { config } from '@/config/axios/config'
import { getAccessToken, getRefreshToken, removeToken, setToken } from '@/utils/auth'
import errorCode from './errorCode'
import { resetRouter } from '@/router'
import { deleteUserCache } from '@/hooks/web/useCache'
import { ApiEncrypt } from '@/utils/encrypt'

const { result_code, base_url, request_timeout } = config

const ignoreMsgs = ['无效的刷新令牌', '刷新令牌已过期']
export const isRelogin = { show: false }
let requestList: Array<() => void> = []
let isRefreshToken = false
const whiteList: string[] = ['/login', '/refresh-token']

const service: AxiosInstance = axios.create({
  baseURL: base_url,
  timeout: request_timeout,
  withCredentials: false,
  paramsSerializer: (params) => qs.stringify(params, { allowDots: true })
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    ;(config as any).silentError = Boolean((config as any).silentError)
    let skipToken = (config.headers || {}).isToken === false
    whiteList.some((url) => {
      if (config.url && config.url.includes(url)) {
        skipToken = true
        return true
      }
      return false
    })

    if (getAccessToken() && !skipToken) {
      config.headers.Authorization = `Bearer ${getAccessToken()}`
    }

    const method = config.method?.toUpperCase()
    if (method === 'GET') {
      config.headers['Cache-Control'] = 'no-cache'
      config.headers['Pragma'] = 'no-cache'
    } else if (method === 'POST') {
      const contentType = config.headers['Content-Type'] || config.headers['content-type']
      if (contentType === 'application/x-www-form-urlencoded' && config.data) {
        config.data = typeof config.data === 'string' ? config.data : qs.stringify(config.data)
      }
    }

    if ((config.headers || {}).isEncrypt && !(config.headers || {}).isEncrypted && config.data) {
      config.data = ApiEncrypt.encryptRequest(config.data)
      config.headers[ApiEncrypt.getEncryptHeader()] = 'true'
    }

    return config
  },
  (error: AxiosError) => Promise.reject(error)
)

service.interceptors.response.use(
  async (response: AxiosResponse<any>) => {
    let { data } = response
    const requestConfig = response.config
    if (!data) throw new Error('Empty response')

    const encryptHeader = ApiEncrypt.getEncryptHeader()
    const isEncryptResponse =
      response.headers[encryptHeader] === 'true' ||
      response.headers[encryptHeader.toLowerCase()] === 'true'
    if (isEncryptResponse && typeof data === 'string') {
      data = ApiEncrypt.decryptResponse(data)
    }

    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      if (response.data.type !== 'application/json') {
        return response.data
      }
      data = await new Response(response.data).json()
    }

    const code = data.code || result_code
    const msg = data.msg || errorCode[code] || errorCode.default

    if (ignoreMsgs.includes(msg)) {
      return Promise.reject(msg)
    }

    if (code === 401) {
      if (!isRefreshToken) {
        isRefreshToken = true
        if (!getRefreshToken()) {
          return handleAuthorized()
        }
        try {
          const refreshTokenRes = await refreshToken()
          setToken(refreshTokenRes.data.data)
          requestConfig.headers!.Authorization = `Bearer ${getAccessToken()}`
          requestList.forEach((cb) => cb())
          requestList = []
          if ((requestConfig.headers || {}).isEncrypt) {
            ;(requestConfig.headers || {}).isEncrypted = true
          }
          return service(requestConfig)
        } catch {
          requestList.forEach((cb) => cb())
          return handleAuthorized()
        } finally {
          requestList = []
          isRefreshToken = false
        }
      }

      return new Promise((resolve) => {
        requestList.push(() => {
          requestConfig.headers!.Authorization = `Bearer ${getAccessToken()}`
          resolve(service(requestConfig))
        })
      })
    }

    if (code === 500) {
      const { t } = useI18n()
      const isSilentError = Boolean((requestConfig as any).silentError)
      if (!isSilentError) {
        ElMessage.error(t('sys.api.errMsg500'))
      }
      return Promise.reject(new Error(msg))
    }

    if (code === 901) {
      const isSilentError = Boolean((requestConfig as any).silentError)
      if (!isSilentError) {
        ElMessage.error({ offset: 300, dangerouslyUseHTMLString: true, message: String(msg) })
      }
      return Promise.reject(new Error(msg))
    }

    if (code !== 200) {
      if (msg === '无效的刷新令牌') {
        return handleAuthorized()
      }
      const isSilentError = Boolean((requestConfig as any).silentError)
      if (!isSilentError) {
        ElNotification.error({ title: msg })
      }
      return Promise.reject(new Error(msg))
    }

    return data
  },
  (error: AxiosError) => {
    let { message } = error
    const { t } = useI18n()
    const isSilentError = Boolean((error.config as any)?.silentError)
    if (message === 'Network Error') {
      message = t('sys.api.errorMessage')
    } else if (message.includes('timeout')) {
      message = t('sys.api.apiTimeoutMessage')
    } else if (message.includes('Request failed with status code')) {
      message = t('sys.api.apiRequestFailed') + message.slice(-3)
    }
    if (!isSilentError) {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

const refreshToken = async () => {
  return await axios.post(base_url + '/admin-api/system/auth/refresh-token?refreshToken=' + getRefreshToken())
}

const handleAuthorized = () => {
  const { t } = useI18n()
  if (!isRelogin.show) {
    if (window.location.href.includes('login')) {
      return Promise.reject(t('sys.api.timeoutMessage'))
    }
    isRelogin.show = true
    ElMessageBox.confirm(t('sys.api.timeoutMessage'), t('common.confirmTitle'), {
      showCancelButton: false,
      closeOnClickModal: false,
      showClose: false,
      closeOnPressEscape: false,
      confirmButtonText: t('login.relogin'),
      type: 'warning'
    }).then(() => {
      resetRouter()
      deleteUserCache()
      removeToken()
      isRelogin.show = false
      window.location.href = window.location.href
    })
  }
  return Promise.reject(t('sys.api.timeoutMessage'))
}

export { service }
