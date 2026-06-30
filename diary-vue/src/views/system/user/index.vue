<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="queryParams.username"
          class="!w-240px"
          clearable
          placeholder="请输入用户名"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          class="!w-240px"
          clearable
          placeholder="请输入手机号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" />重置</el-button>
        <el-button
          v-hasPermi="['system:user:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" />新增
        </el-button>
        <el-button
          v-hasPermi="['system:user:delete']"
          :disabled="checkedIds.length === 0"
          plain
          type="danger"
          @click="handleDeleteBatch"
        >
          <Icon icon="ep:delete" />批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" label="编号" prop="id" />
      <el-table-column align="center" label="用户名" prop="username" show-overflow-tooltip />
      <el-table-column align="center" label="昵称" prop="nickname" show-overflow-tooltip />
      <el-table-column align="center" label="手机号" prop="mobile" width="120" />
      <el-table-column align="center" label="状态">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :disabled="!checkPermi(['system:user:update'])"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="操作" width="220">
        <template #default="scope">
          <el-button
            v-hasPermi="['system:user:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            <Icon icon="ep:edit" />修改
          </el-button>
          <el-dropdown
            v-hasPermi="[
              'system:user:delete',
              'system:user:update-password',
              'system:permission:assign-user-role'
            ]"
            @command="(command) => handleCommand(command, scope.row)"
          >
            <el-button link type="primary"><Icon icon="ep:d-arrow-right" />更多</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="checkPermi(['system:user:delete'])" command="handleDelete">
                  <Icon icon="ep:delete" />删除
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="checkPermi(['system:user:update-password'])"
                  command="handleResetPwd"
                >
                  <Icon icon="ep:key" />重置密码
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="checkPermi(['system:permission:assign-user-role'])"
                  command="handleRole"
                >
                  <Icon icon="ep:circle-check" />分配角色
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

  <UserForm ref="formRef" @success="getList" />
  <UserAssignRoleForm ref="assignRoleFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { checkPermi } from '@/utils/permission'
import { dateFormatter } from '@/utils/formatTime'
import { CommonStatusEnum } from '@/utils/constants'
import * as UserApi from '@/api/system/user'
import UserForm from './UserForm.vue'
import UserAssignRoleForm from './UserAssignRoleForm.vue'

defineOptions({ name: 'SystemUser' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref<UserApi.UserVO[]>([])
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  username: undefined,
  mobile: undefined,
  status: undefined,
  createTime: []
})

const getList = async () => {
  loading.value = true
  try {
    const data = await UserApi.getUserPage(queryParams)
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

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleStatusChange = async (row: UserApi.UserVO) => {
  try {
    const text = row.status === CommonStatusEnum.ENABLE ? '启用' : '停用'
    await message.confirm(`确认要${text}用户 "${row.username}" 吗？`)
    await UserApi.updateUserStatus(row.id, row.status)
    await getList()
  } catch {
    row.status =
      row.status === CommonStatusEnum.ENABLE ? CommonStatusEnum.DISABLE : CommonStatusEnum.ENABLE
  }
}

const handleCommand = (command: string, row: UserApi.UserVO) => {
  if (command === 'handleDelete') handleDelete(row.id)
  if (command === 'handleResetPwd') handleResetPwd(row)
  if (command === 'handleRole') handleRole(row)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await UserApi.deleteUser(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: UserApi.UserVO[]) => {
  checkedIds.value = rows.map((row) => row.id)
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await UserApi.deleteUserList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleResetPwd = async (row: UserApi.UserVO) => {
  try {
    const result = await message.prompt(`请输入用户 "${row.username}" 的新密码`, t('common.reminder'))
    const password = result.value
    await UserApi.resetUserPassword(row.id, password)
    message.success(`修改成功，新密码是：${password}`)
  } catch {}
}

const assignRoleFormRef = ref()
const handleRole = (row: UserApi.UserVO) => {
  assignRoleFormRef.value.open(row)
}

onMounted(() => {
  getList()
})
</script>
