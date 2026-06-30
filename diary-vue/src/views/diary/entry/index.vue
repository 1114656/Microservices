<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          class="!w-240px"
          clearable
          placeholder="请输入日记标题"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select
          v-model="queryParams.categoryId"
          class="!w-220px"
          clearable
          placeholder="请选择"
        >
          <el-option
            v-for="category in categoryList"
            :key="category.id"
            :label="formatCategoryOption(category)"
            :value="category.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-160px" clearable placeholder="请选择">
          <el-option label="草稿" :value="0" />
          <el-option label="发布" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="可见性" prop="visibility">
        <el-select
          v-model="queryParams.visibility"
          class="!w-180px"
          clearable
          placeholder="请选择"
        >
          <el-option label="仅自己" :value="0" />
          <el-option label="登录可见" :value="1" />
          <el-option label="全部可见" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" />重置</el-button>
        <el-button
          v-hasPermi="['diary:entry:create']"
          plain
          type="primary"
          @click="openEditor('create')"
        >
          <Icon icon="ep:plus" />新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="编号" prop="id" width="90" />
      <el-table-column label="标题" min-width="220" prop="title" show-overflow-tooltip />
      <el-table-column align="center" label="分类" width="150">
        <template #default="{ row }">
          <el-tag>{{ formatCategory(row.categoryId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="摘要" min-width="260" prop="summary" show-overflow-tooltip />
      <el-table-column align="center" label="文件" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.fileIds?.length" size="small">{{ row.fileIds.length }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="可见性" width="120">
        <template #default="{ row }">
          <el-tag>{{ formatVisibility(row.visibility) }}</el-tag>
        </template>
      </el-table-column>
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
            v-hasPermi="['diary:entry:update']"
            link
            type="primary"
            @click="openEditor('update', row.id)"
          >
            <Icon icon="ep:edit" />编辑
          </el-button>
          <el-button
            v-hasPermi="['diary:entry:delete']"
            link
            type="danger"
            @click="handleDelete(row.id)"
          >
            <Icon icon="ep:delete" />删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as DiaryApi from '@/api/diary/entry'
import * as DiaryCategoryApi from '@/api/diary/category'

defineOptions({ name: 'DiaryEntry' })

const router = useRouter()
const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref<DiaryApi.DiaryEntryVO[]>([])
const categoryList = ref<DiaryCategoryApi.DiaryCategoryVO[]>([])
const queryFormRef = ref()
const queryParams = reactive<DiaryApi.DiaryEntryPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  title: undefined,
  categoryId: undefined,
  status: undefined,
  visibility: undefined
})

const formatCategory = (categoryId?: number) =>
  categoryList.value.find((category) => category.id === categoryId)?.name || '-'
const formatCategoryOption = (category: DiaryCategoryApi.DiaryCategoryVO) =>
  `${category.name} (${category.totalCount || 0} / 草稿 ${category.draftCount || 0} / 发布 ${
    category.publishedCount || 0
  })`
const formatStatus = (status?: number) => (status === 0 ? '草稿' : '发布')
const formatVisibility = (visibility?: number) => {
  if (visibility === 0) return '仅自己'
  if (visibility === 2) return '全部可见'
  return '登录可见'
}

const getCategoryList = async () => {
  categoryList.value = await DiaryCategoryApi.getDiaryCategoryList()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await DiaryApi.getDiaryPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const openEditor = (type: 'create' | 'update', id?: number) => {
  if (type === 'create') {
    router.push('/content/diary/create')
    return
  }
  router.push({ path: '/content/diary/edit', query: { id } })
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await DiaryApi.deleteDiary(id)
    message.success(t('common.delSuccess'))
    await getCategoryList()
    await getList()
  } catch {}
}

onMounted(async () => {
  await getCategoryList()
  await getList()
})
</script>
