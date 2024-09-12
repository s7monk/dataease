<script lang="ts" setup>
import {reactive, ref, computed, onMounted, watch, toRefs} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import GridTable from "@/components/grid-table/src/GridTable.vue";
import {CustomPassword} from "@/components/custom-password";
import { userListApi, userCreateApi, roleListApi, userUpdateApi, userStatusUpdateApi, userDelApi } from "@/api/user";
import dayjs from 'dayjs'
import { debounce } from 'lodash';
import { FormRules, ElMessage, ElMessageBox, ElPopconfirm } from 'element-plus-secondary'
const { t } = useI18n()

const dialogFormVisible = ref(false)
const editUserVisible = ref(false)
const uname = ref("")
const formRef = ref(null)
const roleOptions = ref([]);
const isEditMode = ref(false);

const fetchRoles = () => {
  const params = {
    pageNum: 1,
    pageSize: 100000000
  }

  roleListApi(params).then(response => {
    roleOptions.value = response.data.data.map(role => ({
      label: role.roleName,
      value: role.id
    }));
  }).catch(error => {
    console.error('Failed to fetch roles:', error);
  });
};

const handleSizeChange = pageSize => {
  state.paginationConfig.currentPage = 1
  state.paginationConfig.pageSize = pageSize
}

const handleCurrentChange = currentPage => {
  state.paginationConfig.currentPage = currentPage
}

const state = reactive({
  paginationConfig: {
    currentPage: 1,
    pageSize: 10,
    total: 0
  },
  tableData: [],
})

const pagingTable = computed(() => {
  const { currentPage, pageSize } = state.paginationConfig
  return state.tableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)
})

interface UserDTO {
  id?: number;
  username: string
  nickname: string
  password: string
  mobile?: string
  email: string
  enabled: true
  roleIds: number[]
}

const form = reactive<UserDTO>({
  id: undefined,
  username: '',
  nickname: '',
  password: '',
  mobile: '',
  email: '',
  enabled: true,
  roleIds: [],
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  roleIds: [
    { required: true, message: '请输选择角色', trigger: 'blur' }
  ],
})

const resetCreateForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const resetForm = () => {
  form.username = '';
  form.nickname = '';
  form.password = '';
  form.mobile = '';
  form.email = '';
  form.enabled = true;
  form.roleIds = [];
};

const openDialog = () => {
  isEditMode.value = false;
  resetCreateForm()
  resetForm()
  dialogFormVisible.value = true
}

const getTableData = () => {
  const params = {
    username: uname.value,
    nickname: uname.value,
    pageNum: state.paginationConfig.currentPage,
    pageSize: state.paginationConfig.pageSize
  }

  userListApi(params).then(res => {
    state.tableData = res.data.data.map(item => {
      const {id, username, nickname, mobile, email, enabled, createTime, roles} = item
      return {
        id,
        username,
        nickname,
        mobile,
        email,
        enabled,
        createTime: dayjs(createTime).format('YYYY-MM-DD HH:mm:ss'),
        roleIds: roles.map(role => role.id)
      }
    })
    state.paginationConfig.total = res.data.total
  })
}

const debouncedGetTableData  = debounce(getTableData, 300);

watch(uname, () => {
  debouncedGetTableData();
});


const handleCreateOrUpdate = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      let apiData = { ...form };
      if (isEditMode.value) {
        delete apiData.password;
      }

      const apiCall = isEditMode.value ? userUpdateApi : userCreateApi;
      apiCall(apiData).then(res => {
        if (res.data.code === 200) {
          ElMessage.success(isEditMode.value ? '用户更新成功' : '用户添加成功');
          dialogFormVisible.value = false;
          editUserVisible.value = false;
          resetForm();
          getTableData();
        }
      }).catch(err => {
        ElMessage.error(isEditMode.value ? '用户更新失败，请重试' : '用户添加失败，请重试');
      });
    } else {
      ElMessage.error('请填写正确的用户信息');
      return false;
    }
  });
};
const openEditDialog = (user) => {
  isEditMode.value = true;
  form.id = user.id;
  form.username = user.username;
  form.nickname = user.nickname;
  form.mobile = user.mobile;
  form.email = user.email;
  form.enabled = user.enabled;
  form.roleIds = user.roleIds;

  editUserVisible.value = true;
};

const cancel = () => {
  resetForm()
  dialogFormVisible.value = false
}

const cancelEdit = () => {
  resetForm();
  editUserVisible.value = false;
}


const confirmUpdateUserStatus = (userId: number, enabled: boolean) => {
  const message = enabled ? '你确定要启用此用户吗?' : '你确定要封禁此用户吗?';

  ElMessageBox.confirm(
    message,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      showClose: false
    },
  ).then(() => {
    updateUserStatus(userId, enabled);
  }).catch(() => {
    const user = state.tableData.find(u => u.id === userId);
    if (user) {
      user.enabled = !enabled;
    }
  });
};

const updateUserStatus = (userId: number, enabled: boolean) => {
  const data = { id: userId, enabled };
  userStatusUpdateApi(data)
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success('用户状态更新成功');
        getTableData();
      } else {
        throw new Error('Failed to update status');
      }
    })
    .catch(error => {
      console.error('Error updating user status:', error);
      ElMessage.error('用户状态更新失败，请重试');
      const user = state.tableData.find(u => u.id === userId);
      if (user) {
        user.enabled = !enabled;
      }
    });
};

const confirmDeleteUser = (userId) => {
  ElMessageBox.confirm(
    '您确定要删除该用户吗？',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true,
      showClose: false,
      confirmButtonType: 'danger',
    }
  ).then(() => {
    deleteUser(userId);
  }).catch(() => {
  });
};

const deleteUser = (userId) => {
  userDelApi(userId).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('用户删除成功');
      getTableData();
    } else {
      throw new Error('Failed to delete user');
    }
  }).catch(error => {
    console.error('Error deleting user:', error);
    ElMessage.error('用户删除失败，请重试');
  });
};

onMounted(() => {
  getTableData();
  fetchRoles();
});
</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.user_manager') }}</p>
  </div>
  <div class="container-content">
    <el-row class="top-operate-content">
      <el-col :span="12">
<!--        <el-button type="primary" @click="dialogFormVisible = true">-->
        <el-button type="primary" @click="openDialog">
          <template #icon>
            <Icon name="icon_add_outlined" />
          </template>
          添加用户
        </el-button>
      </el-col>
      <el-col
        :span="12"
        class="right-filter"
      >
        <el-input
          ref="search"
          v-model="uname"
          :placeholder="t('user.search_placeholder')"
          class="search-placeholder"
          clearable
        >
          <template #prefix>
            <el-icon>
              <Icon name="icon_search-outline_outlined" />
            </el-icon>
          </template>
        </el-input>
      </el-col>
    </el-row>
    <div class="info-table">
      <grid-table
        :show-pagination="true"
        :pagination="state.paginationConfig"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :table-data="pagingTable"
      >
        <el-table-column
          key="username"
          prop="username"
          label="用户名"
          resizable
          sortable
          show-overflow-tooltip
        />
        <el-table-column
          key="nickname"
          prop="nickname"
          label="昵称"
          sortable
          resizable
          show-overflow-tooltip
        />
        <el-table-column
          key="mobile"
          prop="mobile"
          label="手机号"
          resizable
          show-overflow-tooltip
        />
        <el-table-column
          key="email"
          prop="email"
          label="邮箱"
          resizable
          show-overflow-tooltip
        />
        <el-table-column
          key="enabled"
          prop="enabled"
          label="状态"
          width="80"
        >
          <template #default="scope">
            <el-switch
              v-model="scope.row.enabled"
              :active-value="true"
              :inactive-value="false"
              inactive-color="#DCDFE6"
              @change="confirmUpdateUserStatus(scope.row.id, scope.row.enabled)"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="createTime"
          prop="createTime"
          label="创建时间"
          sortable
          resizable
          show-overflow-tooltip
          width="200"
        />
        <el-table-column
          key="operate"
          prop="operate"
          label="操作"
          fixed="right"
          width="148"
        >
          <template #default="scope">
            <el-button
              text
              v-permission="['dataset']"
              @click="openEditDialog(scope.row)"
            >
              <template #icon>
                <Icon name="icon_edit_outlined"></Icon>
              </template>
            </el-button>
            <el-button
              text
              v-permission="['dataset']"
              @click="confirmDeleteUser(scope.row.id)"
            >
              <template #icon>
                <Icon name="icon_delete-trash_outlined"></Icon>
              </template>
            </el-button>
          </template>
        </el-table-column>
      </grid-table>
    </div>
    <el-dialog
      title="添加用户"
      v-model="dialogFormVisible"
      width="560px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        style="max-width: 560px"
        label-position="left"
        require-asterisk-position="right"
        label-width="auto">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="密码"  prop="password">
          <CustomPassword
            v-model="form.password"
            placeholder="请输入密码"
            show-password
            maxlength="30"
            show-word-limit
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="手机号"  prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="邮箱"  prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" placeholder="请选择角色" multiple style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role.value" :label="role.label" :value="role.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="handleCreateOrUpdate">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      title="编辑用户"
      v-model="editUserVisible"
      width="560px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        style="max-width: 560px"
        label-position="left"
        require-asterisk-position="right"
        label-width="auto">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="手机号"  prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="邮箱"  prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" placeholder="请选择角色" multiple style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role.value" :label="role.label" :value="role.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="handleCreateOrUpdate">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<style lang="less" scoped>
.router-title {
  color: #1f2329;
  font-feature-settings: 'clig' off, 'liga' off;
  font-family: '阿里巴巴普惠体 3.0 55 Regular L3';
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: 28px;
  float: left;
}
.container-content {
  width: 100%;
  background: #ffffff;
  height: calc(100vh - 140px);
  box-sizing: border-box;
  border-radius: 4px;
  margin-top: 16px;
  padding: 24px;
}
.container-header {
  height: 32px;
}

.search-placeholder {
  width: 240px
}

.info-table {
  height: calc(100% - 49px);
}
</style>
