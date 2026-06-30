<template>
  <ContentWrap>
    <el-form ref="formRef" :model="formData" label-width="110px">
      <el-form-item label="首页展示">
        <el-radio-group v-model="formData.homePageType">
          <el-radio-button label="diary">日记</el-radio-button>
          <el-radio-button label="blog">博客</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button
          v-hasPermi="['site:config:update']"
          :loading="saving"
          type="primary"
          @click="submitForm"
        >
          <Icon icon="ep:check" />保存
        </el-button>
        <el-button @click="getConfig"><Icon icon="ep:refresh" />刷新</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>

<script lang="ts" setup>
import {
  DEFAULT_HOME_PAGE_TYPE,
  getSiteConfig,
  updateSiteConfig,
  type SiteConfigVO
} from '@/api/site/config'

defineOptions({ name: 'SiteSettings' })

const message = useMessage()
const saving = ref(false)
const formData = reactive<SiteConfigVO>({
  homePageType: DEFAULT_HOME_PAGE_TYPE
})

const getConfig = async () => {
  const data = await getSiteConfig()
  formData.homePageType = data?.homePageType || DEFAULT_HOME_PAGE_TYPE
}

const submitForm = async () => {
  saving.value = true
  try {
    await updateSiteConfig(formData)
    message.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(() => getConfig())
</script>
