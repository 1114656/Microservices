<template>
  <div class="diary-editor-page">
    <ContentWrap>
      <div class="editor-header">
        <div>
          <div class="editor-title">{{ isEdit ? '编辑日记' : '新增日记' }}</div>
          <div class="editor-subtitle">用文字、图片和文件块组合一篇完整日记</div>
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
            <el-input v-model="formData.title" maxlength="100" placeholder="请输入日记标题" />
          </el-form-item>

          <div class="block-toolbar">
            <el-button plain type="primary" @click="addTextBlock"><Icon icon="ep:edit-pen" />文字</el-button>
            <el-upload accept="image/*" :http-request="uploadImageBlock" :show-file-list="false">
              <el-button plain><Icon icon="ep:picture" />图片</el-button>
            </el-upload>
            <el-upload :http-request="uploadFileBlock" :show-file-list="false">
              <el-button plain><Icon icon="ep:upload" />文件</el-button>
            </el-upload>
          </div>

          <el-empty v-if="formData.contentBlocks.length === 0" description="还没有正文内容" />

          <div
            v-for="(block, index) in formData.contentBlocks"
            :key="block.localId"
            class="content-block"
          >
            <div class="block-head">
              <el-tag :type="block.type === 'text' ? 'success' : block.isImage ? 'primary' : 'warning'">
                {{ block.type === 'text' ? '文字' : block.isImage ? '图片' : '文件' }}
              </el-tag>
              <div class="block-actions">
                <el-button :disabled="index === 0" link type="primary" @click="moveBlock(index, -1)">
                  上移
                </el-button>
                <el-button
                  :disabled="index === formData.contentBlocks.length - 1"
                  link
                  type="primary"
                  @click="moveBlock(index, 1)"
                >
                  下移
                </el-button>
                <el-button link type="danger" @click="removeBlock(index)">删除</el-button>
              </div>
            </div>

            <el-input
              v-if="block.type === 'text'"
              v-model="block.content"
              :autosize="{ minRows: 5, maxRows: 16 }"
              placeholder="写点什么..."
              type="textarea"
            />

            <div v-else-if="block.isImage" class="image-block">
              <el-image
                v-if="block.previewUrl"
                :preview-src-list="[block.previewUrl]"
                :src="block.previewUrl"
                fit="cover"
              />
              <div v-else class="image-preview-empty">
                <Icon icon="ep:picture" :size="28" />
                <span>{{ block.previewError ? '图片预览加载失败' : '图片预览加载中' }}</span>
              </div>
              <div class="file-meta">
                <div class="file-name">{{ block.fileName || `图片 #${block.fileId}` }}</div>
                <div class="file-id">文件 ID：{{ block.fileId }}</div>
              </div>
            </div>

            <div v-else class="file-block">
              <Icon icon="ep:document" :size="28" />
              <div class="file-meta">
                <div class="file-name">{{ block.fileName || `文件 #${block.fileId}` }}</div>
                <div class="file-id">文件 ID：{{ block.fileId }}</div>
              </div>
            </div>
          </div>
        </el-form>
      </ContentWrap>

      <ContentWrap class="editor-side">
        <el-form :model="formData" :rules="formRules" label-position="top">
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="formData.categoryId" class="w-full" placeholder="请选择分类">
              <el-option
                v-for="category in enabledCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
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
import * as DiaryApi from '@/api/diary/entry'
import * as DiaryCategoryApi from '@/api/diary/category'
import * as FileApi from '@/api/file'

defineOptions({ name: 'DiaryEntryEditor' })

interface EditorBlock extends DiaryApi.DiaryContentBlockVO {
  localId: string
  fileName?: string
  contentType?: string
  fileCategory?: string
  extension?: string
  previewUrl?: string
  isImage?: boolean
  previewError?: boolean
}

const route = useRoute()
const router = useRouter()
const message = useMessage()
const formRef = ref()
const saving = ref(false)
const categoryList = ref<DiaryCategoryApi.DiaryCategoryVO[]>([])
const formData = reactive<DiaryApi.DiaryEntryVO & { contentBlocks: EditorBlock[] }>({
  id: undefined,
  title: '',
  categoryId: undefined,
  contentBlocks: [],
  status: 1,
  visibility: 1
})
const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '分类不能为空', trigger: 'change' }]
})

const isEdit = computed(() => Boolean(route.query.id))
const enabledCategories = computed(() => categoryList.value.filter((category) => category.status !== 0))

const newLocalId = () => `${Date.now()}-${Math.random().toString(16).slice(2)}`

const goBack = () => {
  router.push('/content/diary')
}

const addTextBlock = () => {
  formData.contentBlocks.push({ localId: newLocalId(), type: 'text', content: '' })
}

const uploadImageBlock = async (options: any) => {
  await uploadFileAsBlock(options, true)
}

const uploadFileBlock = async (options: any) => {
  await uploadFileAsBlock(options, false)
}

const uploadFileAsBlock = async (options: any, forceImage: boolean) => {
  try {
    const file = await FileApi.uploadFile(options.file, { businessType: 'diary' })
    const block: EditorBlock = {
      localId: newLocalId(),
      type: 'file',
      fileId: file.id,
      fileName: file.originalName,
      contentType: file.contentType,
      fileCategory: file.fileCategory,
      extension: file.extension,
      isImage: forceImage || isImageFile(file)
    }
    if (block.isImage) {
      await fillPreview(block)
    }
    formData.contentBlocks.push(block)
    options.onSuccess?.(file)
  } catch (error) {
    options.onError?.(error)
  }
}

const fillPreview = async (block: EditorBlock) => {
  if (!block.fileId) return
  try {
    const data = await FileApi.getFilePreviewUrl(block.fileId)
    block.previewUrl = data.url || data.previewUrl
    block.previewError = !block.previewUrl
  } catch {
    block.previewError = true
  }
}

const hydrateFileBlock = async (block: EditorBlock) => {
  if (!block.fileId) return
  try {
    const file = await FileApi.getFile(block.fileId)
    block.fileName = file.originalName
    block.contentType = file.contentType
    block.fileCategory = file.fileCategory
    block.extension = file.extension
    block.isImage = isImageFile(file)
    if (block.isImage) {
      await fillPreview(block)
    }
  } catch {
    block.fileName = `文件 #${block.fileId}`
  }
}

const isImageFile = (file: Partial<FileApi.FileObjectVO>) => {
  const contentType = file.contentType?.toLowerCase() || ''
  const category = file.fileCategory?.toLowerCase() || ''
  const extension = file.extension?.toLowerCase() || ''
  return (
    contentType.startsWith('image/') ||
    category === 'image' ||
    ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg'].includes(extension)
  )
}

const moveBlock = (index: number, offset: number) => {
  const nextIndex = index + offset
  const current = formData.contentBlocks[index]
  formData.contentBlocks.splice(index, 1)
  formData.contentBlocks.splice(nextIndex, 0, current)
}

const removeBlock = (index: number) => {
  formData.contentBlocks.splice(index, 1)
}

const buildPayload = () => ({
  ...formData,
  contentBlocks: formData.contentBlocks
    .filter((block) => (block.type === 'text' ? Boolean(block.content?.trim()) : Boolean(block.fileId)))
    .map((block) =>
      block.type === 'text'
        ? { type: 'text' as const, content: block.content }
        : { type: 'file' as const, fileId: block.fileId }
    )
})

const submit = async (status: number) => {
  await formRef.value.validate()
  formData.status = status
  saving.value = true
  try {
    const payload = buildPayload()
    if (isEdit.value) {
      await DiaryApi.updateDiary(payload)
      message.success(status === 1 ? '发布成功' : '草稿已保存')
    } else {
      await DiaryApi.createDiary(payload)
      message.success(status === 1 ? '发布成功' : '草稿已保存')
    }
    goBack()
  } finally {
    saving.value = false
  }
}

const loadData = async () => {
  categoryList.value = await DiaryCategoryApi.getDiaryCategoryList()
  const id = Number(route.query.id)
  if (!id) {
    addTextBlock()
    return
  }
  const data = await DiaryApi.getDiary(id)
  Object.assign(formData, data)
  formData.contentBlocks = (data.contentBlocks || []).map((block) => ({ ...block, localId: newLocalId() }))
  await Promise.all(formData.contentBlocks.filter((block) => block.type === 'file').map(hydrateFileBlock))
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.diary-editor-page {
  .editor-header,
  .editor-actions,
  .block-toolbar,
  .block-head,
  .block-actions,
  .image-block,
  .file-block {
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
  .block-toolbar,
  .block-actions {
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

  .block-toolbar {
    margin: 4px 0 16px;
  }

  .content-block {
    padding: 14px;
    margin-bottom: 12px;
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    background: var(--el-bg-color);
  }

  .block-head {
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .image-block,
  .file-block {
    gap: 12px;
  }

  .image-block :deep(.el-image),
  .image-preview-empty {
    width: 180px;
    height: 120px;
    border-radius: 6px;
    border: 1px solid var(--el-border-color);
    overflow: hidden;
  }

  .image-preview-empty {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-secondary);
    background: var(--el-fill-color-light);
  }

  .file-block {
    padding: 14px;
    border-radius: 6px;
    background: var(--el-fill-color-light);
  }

  .file-name {
    font-weight: 500;
    color: var(--el-text-color-primary);
  }

  .file-id {
    margin-top: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}

@media (max-width: 1100px) {
  .diary-editor-page {
    .editor-layout {
      grid-template-columns: 1fr;
    }

    .editor-side {
      position: static;
    }
  }
}
</style>
