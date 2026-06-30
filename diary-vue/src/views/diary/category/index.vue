<template>
  <ContentWrap>
    <el-form :inline="true" class="-mb-15px">
      <el-form-item>
        <el-button v-hasPermi="['diary:category:create']" plain type="primary" @click="openForm('create')">
          <Icon icon="ep:plus" />新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="编号" prop="id" width="90" />
      <el-table-column label="分类名称" min-width="180" prop="name" show-overflow-tooltip />
      <el-table-column align="center" label="排序" prop="sort" width="90" />
      <el-table-column align="center" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="日记总数" prop="totalCount" width="100" />
      <el-table-column align="center" label="草稿" prop="draftCount" width="90" />
      <el-table-column align="center" label="发布" prop="publishedCount" width="90" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="{ row }">
          <el-button
            v-hasPermi="['diary:category:update']"
            link
            type="primary"
            @click="openForm('update', row)"
          >
            <Icon icon="ep:edit" />编辑
          </el-button>
          <el-button
            v-hasPermi="['diary:category:delete']"
            link
            type="danger"
            @click="handleDelete(row.id)"
          >
            <Icon icon="ep:delete" />删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <Dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="86px">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="formData.name" maxlength="50" placeholder="请输入分类名称" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="formData.sort" :min="0" class="!w-180px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio-button :label="1">启用</el-radio-button>
          <el-radio-button :label="0">停用</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">保存</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as DiaryCategoryApi from '@/api/diary/category'

defineOptions({ name: 'DiaryCategory' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(false)
const list = ref<DiaryCategoryApi.DiaryCategoryVO[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref<'create' | 'update'>('create')
const formRef = ref()
const formData = reactive<DiaryCategoryApi.DiaryCategoryVO>({
  id: 0,
  name: '',
  sort: 0,
  status: 1
})
const formRules = reactive({
  name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }]
})

const getList = async () => {
  loading.value = true
  try {
    list.value = await DiaryCategoryApi.getDiaryCategoryList()
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.id = 0
  formData.name = ''
  formData.sort = 0
  formData.status = 1
  formRef.value?.resetFields()
}

const openForm = (type: 'create' | 'update', row?: DiaryCategoryApi.DiaryCategoryVO) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增分类' : '编辑分类'
  formType.value = type
  resetForm()
  if (row) {
    Object.assign(formData, row)
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = { ...formData }
    if (formType.value === 'create') {
      await DiaryCategoryApi.createDiaryCategory(data)
      message.success('新增成功')
    } else {
      await DiaryCategoryApi.updateDiaryCategory(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    await getList()
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await DiaryCategoryApi.deleteDiaryCategory(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
