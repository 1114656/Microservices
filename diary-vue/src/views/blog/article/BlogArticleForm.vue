<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="820px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="86px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="formData.title" maxlength="120" placeholder="请输入博客标题" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio-button :label="0">草稿</el-radio-button>
          <el-radio-button :label="1">发布</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="可见性" prop="visibility">
        <el-radio-group v-model="formData.visibility">
          <el-radio-button :label="0">仅自己</el-radio-button>
          <el-radio-button :label="1">登录可见</el-radio-button>
          <el-radio-button :label="2">全部可见</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="封面文件">
        <div class="flex items-center gap-8px">
          <el-input-number v-model="formData.coverFileId" :min="1" class="!w-200px" />
          <el-upload :http-request="uploadCover" :show-file-list="false">
            <el-button plain><Icon icon="ep:upload" />上传封面</el-button>
          </el-upload>
          <span v-if="coverName" class="text-[var(--el-text-color-secondary)]">{{ coverName }}</span>
        </div>
      </el-form-item>
      <el-form-item label="正文" prop="contentMarkdown">
        <div class="w-full">
          <div class="mb-8px flex gap-8px">
            <el-button plain @click="insertCodeBlock('java')">
              <Icon icon="ep:document" />Java
            </el-button>
            <el-button plain @click="insertCodeBlock('typescript')">
              <Icon icon="ep:document" />TypeScript
            </el-button>
            <el-button plain @click="insertCodeBlock('sql')">
              <Icon icon="ep:document" />SQL
            </el-button>
          </div>
          <el-input
            v-model="formData.contentMarkdown"
            :autosize="{ minRows: 14, maxRows: 26 }"
            placeholder="请输入 Markdown 内容，代码用 ```language 包裹"
            type="textarea"
          />
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as BlogApi from '@/api/blog/article'
import * as FileApi from '@/api/file'

defineOptions({ name: 'BlogArticleForm' })

const message = useMessage()
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref<'create' | 'update'>('create')
const formRef = ref()
const coverName = ref('')
const formData = reactive<BlogApi.BlogArticleVO>({
  title: '',
  contentMarkdown: '',
  coverFileId: undefined,
  status: 1,
  visibility: 1
})
const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  contentMarkdown: [{ required: true, message: '正文不能为空', trigger: 'blur' }]
})

const resetForm = () => {
  formData.id = undefined
  formData.title = ''
  formData.contentMarkdown = ''
  formData.coverFileId = undefined
  formData.status = 1
  formData.visibility = 1
  coverName.value = ''
  formRef.value?.resetFields()
}

const open = async (type: 'create' | 'update', id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增博客' : '编辑博客'
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await BlogApi.getBlogArticle(id)
      Object.assign(formData, data)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const uploadCover = async (options: any) => {
  try {
    const file = await FileApi.uploadFile(options.file, { businessType: 'blog_cover' })
    formData.coverFileId = file.id
    coverName.value = file.originalName
    options.onSuccess?.(file)
  } catch (error) {
    options.onError?.(error)
  }
}

const insertCodeBlock = (language: string) => {
  const codeBlock = `\n\n\`\`\`${language}\n\n\`\`\`\n`
  formData.contentMarkdown = `${formData.contentMarkdown || ''}${codeBlock}`
}

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = { ...formData }
    if (formType.value === 'create') {
      await BlogApi.createBlogArticle(data)
      message.success('新增成功')
    } else {
      await BlogApi.updateBlogArticle(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
