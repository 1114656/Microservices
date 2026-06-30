<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="760px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="86px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="formData.title" maxlength="100" placeholder="请输入日记标题" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="formData.categoryId" class="!w-260px" placeholder="请选择分类">
          <el-option
            v-for="category in enabledCategories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
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
      <el-form-item label="正文块">
        <div class="w-full flex flex-col gap-10px">
          <div class="flex gap-8px">
            <el-button plain type="primary" @click="addTextBlock">
              <Icon icon="ep:plus" />文字
            </el-button>
            <el-upload :http-request="uploadFileBlock" :show-file-list="false">
              <el-button plain><Icon icon="ep:upload" />插入文件</el-button>
            </el-upload>
          </div>
          <el-empty v-if="formData.contentBlocks.length === 0" description="暂无正文块" />
          <div
            v-for="(block, index) in formData.contentBlocks"
            :key="index"
            class="border border-[var(--el-border-color)] rounded-4px p-10px"
          >
            <div class="mb-8px flex items-center justify-between gap-8px">
              <el-tag :type="block.type === 'text' ? 'success' : 'warning'">
                {{ block.type === 'text' ? '文字' : '文件' }}
              </el-tag>
              <div>
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
              :autosize="{ minRows: 3, maxRows: 8 }"
              placeholder="请输入文字内容"
              type="textarea"
            />
            <el-input-number v-else v-model="block.fileId" :min="1" class="!w-240px" />
          </div>
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
import * as DiaryApi from '@/api/diary/entry'
import * as DiaryCategoryApi from '@/api/diary/category'
import * as FileApi from '@/api/file'

defineOptions({ name: 'DiaryEntryForm' })

const message = useMessage()
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref<'create' | 'update'>('create')
const formRef = ref()
const categoryList = ref<DiaryCategoryApi.DiaryCategoryVO[]>([])
const formData = reactive<DiaryApi.DiaryEntryVO>({
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
const enabledCategories = computed(() => categoryList.value.filter((category) => category.status !== 0))

const resetForm = () => {
  formData.id = undefined
  formData.title = ''
  formData.categoryId = undefined
  formData.contentBlocks = []
  formData.status = 1
  formData.visibility = 1
  formRef.value?.resetFields()
}

const open = async (type: 'create' | 'update', id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增日记' : '编辑日记'
  formType.value = type
  resetForm()
  categoryList.value = await DiaryCategoryApi.getDiaryCategoryList()
  if (id) {
    formLoading.value = true
    try {
      const data = await DiaryApi.getDiary(id)
      Object.assign(formData, data)
      formData.contentBlocks = data.contentBlocks || []
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const addTextBlock = () => {
  formData.contentBlocks.push({ type: 'text', content: '' })
}

const uploadFileBlock = async (options: any) => {
  try {
    const file = await FileApi.uploadFile(options.file, { businessType: 'diary' })
    formData.contentBlocks.push({ type: 'file', fileId: file.id })
    options.onSuccess?.(file)
  } catch (error) {
    options.onError?.(error)
  }
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

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = {
      ...formData,
      contentBlocks: formData.contentBlocks.filter((block) =>
        block.type === 'text' ? Boolean(block.content?.trim()) : Boolean(block.fileId)
      )
    }
    if (formType.value === 'create') {
      await DiaryApi.createDiary(data)
      message.success('新增成功')
    } else {
      await DiaryApi.updateDiary(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
