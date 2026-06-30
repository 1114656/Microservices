<template>
  <div class="blog-editor-page">
    <ContentWrap>
      <div class="editor-header">
        <div>
          <div class="editor-title">{{ isEdit ? '编辑博客' : '新增博客' }}</div>
          <div class="editor-subtitle">Markdown 编写，右侧实时预览</div>
        </div>
        <div class="editor-actions">
          <el-button @click="goBack"><Icon icon="ep:back" />返回</el-button>
          <el-button :loading="saving" @click="submit(0)">保存草稿</el-button>
          <el-button :loading="saving" type="primary" @click="submit(1)">
            <Icon icon="ep:promotion" />发布
          </el-button>
        </div>
      </div>
    </ContentWrap>

    <div class="editor-layout">
      <ContentWrap class="editor-main">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-position="top">
          <el-form-item label="标题" prop="title">
            <el-input v-model="formData.title" maxlength="120" placeholder="请输入博客标题" />
          </el-form-item>
          <div class="markdown-layout">
            <div class="markdown-pane">
              <div class="pane-title">正文</div>
              <div class="code-toolbar">
                <el-button plain @click="insertCodeBlock('java')"><Icon icon="ep:document" />Java</el-button>
                <el-button plain @click="insertCodeBlock('typescript')">
                  <Icon icon="ep:document" />TypeScript
                </el-button>
                <el-button plain @click="insertCodeBlock('sql')"><Icon icon="ep:document" />SQL</el-button>
              </div>
              <el-form-item prop="contentMarkdown">
                <el-input
                  v-model="formData.contentMarkdown"
                  :autosize="{ minRows: 22, maxRows: 34 }"
                  placeholder="请输入 Markdown 内容"
                  type="textarea"
                />
              </el-form-item>
            </div>
            <div class="markdown-pane preview-pane">
              <div class="pane-title">预览</div>
              <div class="preview-content">
                <MarkdownView v-if="formData.contentMarkdown" :content="formData.contentMarkdown" />
                <el-empty v-else description="输入正文后在这里预览" />
              </div>
            </div>
          </div>
        </el-form>
      </ContentWrap>

      <ContentWrap class="editor-side">
        <el-form :model="formData" label-position="top">
          <el-form-item label="封面">
            <div class="cover-box">
              <el-image v-if="coverPreviewUrl" :src="coverPreviewUrl" fit="cover" />
              <div v-else class="cover-empty">
                <Icon icon="ep:picture" :size="28" />
                <span>暂无封面</span>
              </div>
              <el-upload accept="image/*" :http-request="uploadCover" :show-file-list="false">
                <el-button class="mt-10px" plain><Icon icon="ep:upload" />上传封面</el-button>
              </el-upload>
              <div v-if="coverName" class="cover-name">{{ coverName }}</div>
            </div>
          </el-form-item>
          <el-form-item label="当前状态">
            <el-radio-group v-model="formData.status">
              <el-radio-button :label="0">草稿</el-radio-button>
              <el-radio-button :label="1">发布</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="可见性">
            <el-radio-group v-model="formData.visibility">
              <el-radio-button :label="0">仅自己</el-radio-button>
              <el-radio-button :label="1">登录可见</el-radio-button>
              <el-radio-button :label="2">全部可见</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </ContentWrap>
    </div>
  </div>
</template>

<script lang="ts" setup>
import MarkdownView from '@/components/MarkdownView/index.vue'
import * as BlogApi from '@/api/blog/article'
import * as FileApi from '@/api/file'

defineOptions({ name: 'BlogArticleEditor' })

const route = useRoute()
const router = useRouter()
const message = useMessage()
const formRef = ref()
const saving = ref(false)
const coverName = ref('')
const coverPreviewUrl = ref('')
const formData = reactive<BlogApi.BlogArticleVO>({
  id: undefined,
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

const isEdit = computed(() => Boolean(route.query.id))

const goBack = () => {
  router.push('/content/blog')
}

const uploadCover = async (options: any) => {
  try {
    const file = await FileApi.uploadFile(options.file, { businessType: 'blog_cover' })
    formData.coverFileId = file.id
    coverName.value = file.originalName
    await fillCoverPreview(file.id)
    options.onSuccess?.(file)
  } catch (error) {
    options.onError?.(error)
  }
}

const fillCoverPreview = async (fileId?: number) => {
  if (!fileId) {
    coverPreviewUrl.value = ''
    return
  }
  try {
    const data = await FileApi.getFilePreviewUrl(fileId)
    coverPreviewUrl.value = data.url || data.previewUrl || ''
  } catch {
    coverPreviewUrl.value = ''
  }
}

const hydrateCover = async () => {
  if (!formData.coverFileId) return
  try {
    const file = await FileApi.getFile(formData.coverFileId)
    coverName.value = file.originalName
    if (file.contentType?.startsWith('image/')) {
      await fillCoverPreview(file.id)
    }
  } catch {}
}

const insertCodeBlock = (language: string) => {
  const codeBlock = `\n\n\`\`\`${language}\n\n\`\`\`\n`
  formData.contentMarkdown = `${formData.contentMarkdown || ''}${codeBlock}`
}

const submit = async (status: number) => {
  await formRef.value.validate()
  formData.status = status
  saving.value = true
  try {
    const payload = { ...formData }
    if (isEdit.value) {
      await BlogApi.updateBlogArticle(payload)
      message.success(status === 1 ? '发布成功' : '草稿已保存')
    } else {
      await BlogApi.createBlogArticle(payload)
      message.success(status === 1 ? '发布成功' : '草稿已保存')
    }
    goBack()
  } finally {
    saving.value = false
  }
}

const loadData = async () => {
  const id = Number(route.query.id)
  if (!id) return
  const data = await BlogApi.getBlogArticle(id)
  Object.assign(formData, data)
  await hydrateCover()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.blog-editor-page {
  .editor-header,
  .editor-actions,
  .code-toolbar {
    display: flex;
    align-items: center;
  }

  .editor-header {
    justify-content: space-between;
    gap: 16px;
  }

  .editor-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .editor-subtitle {
    margin-top: 4px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  .editor-actions,
  .code-toolbar {
    gap: 8px;
  }

  .editor-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 320px;
    gap: 16px;
  }

  .editor-main,
  .editor-side {
    min-width: 0;
  }

  .editor-side {
    align-self: start;
    position: sticky;
    top: 12px;
  }

  .markdown-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
    gap: 16px;
  }

  .pane-title {
    margin-bottom: 10px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .code-toolbar {
    margin-bottom: 10px;
  }

  .preview-content {
    min-height: 532px;
    padding: 14px;
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    background: var(--el-fill-color-extra-light);
    overflow: auto;
  }

  .cover-box {
    width: 100%;
  }

  .cover-box :deep(.el-image),
  .cover-empty {
    width: 100%;
    height: 160px;
    border-radius: 6px;
    border: 1px solid var(--el-border-color);
    overflow: hidden;
  }

  .cover-empty {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-secondary);
    background: var(--el-fill-color-light);
  }

  .cover-name {
    margin-top: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    word-break: break-all;
  }
}

@media (max-width: 1280px) {
  .blog-editor-page {
    .editor-layout,
    .markdown-layout {
      grid-template-columns: 1fr;
    }

    .editor-side {
      position: static;
    }
  }
}
</style>
