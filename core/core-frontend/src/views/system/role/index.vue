<script lang="ts" setup>
import {ref, reactive, computed, watch, onMounted} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import GridTable from "@/components/grid-table/src/GridTable.vue";
import {ElMessage, ElMessageBox, FormRules} from "element-plus-secondary";
import {roleListApi, roleStatusUpdateApi} from "@/api/user";
import dayjs from "dayjs";
import {debounce} from "lodash";
import {CustomPassword} from "@/components/custom-password";

const { t } = useI18n()
const uname = ref("")
const dialogFormVisible = ref(false)
const isEditMode = ref(false);
const formRef = ref(null)

const state = reactive({
  paginationConfig: {
    currentPage: 1,
    pageSize: 10,
    total: 0
  },
  tableData: [],
})

const handleSizeChange = pageSize => {
  state.paginationConfig.currentPage = 1
  state.paginationConfig.pageSize = pageSize
}

const handleCurrentChange = currentPage => {
  state.paginationConfig.currentPage = currentPage
}

const pagingTable = computed(() => {
  const { currentPage, pageSize } = state.paginationConfig
  return state.tableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)
})

interface RoleDTO {
  id?: number;
  roleName: string
  roleKey: string
  enabled: true
  remark?: string
  menuIds: number[]
  indeterminateKeys?: number[]
}

const form = reactive<RoleDTO>({
  id: undefined,
  roleName: '',
  roleKey: '',
  enabled: true,
  remark: '',
  menuIds: [],
  indeterminateKeys: [],
})

const getTableData = () => {
  const params = {
    roleName: uname.value,
    pageNum: state.paginationConfig.currentPage,
    pageSize: state.paginationConfig.pageSize
  }

  roleListApi(params).then(res => {
    state.tableData = res.data.data.map(item => {
      const {id, roleName, roleKey, enabled, remark, createTime, menuIds, indeterminateKeys} = item
      return {
        id,
        roleName,
        roleKey,
        enabled,
        remark,
        createTime: dayjs(createTime).format('YYYY-MM-DD HH:mm:ss'),
        menuIds,
        indeterminateKeys
      }
    })
    state.paginationConfig.total = res.data.total
  })
}

const debouncedGetTableData  = debounce(getTableData, 300);

watch(uname, () => {
  debouncedGetTableData();
});

const updateRoleStatus = (roleId: number, enabled: boolean) => {
  const data = { id: roleId, enabled };
  roleStatusUpdateApi(data)
    .then(response => {
      if (response.data.code === 200) {
        ElMessage.success('角色状态更新成功');
        getTableData();
      } else {
        throw new Error('Failed to update status');
      }
    })
    .catch(error => {
      console.error('Error updating user status:', error);
      ElMessage.error('角色状态更新失败，请重试');
      const role = state.tableData.find(u => u.id === roleId);
      if (role) {
        role.enabled = !enabled;
      }
    });
};

const confirmUpdateRoleStatus = (roleId: number, enabled: boolean) => {
  const message = enabled ? '你确定要启用此角色吗?' : '你确定要封禁此角色吗?';

  ElMessageBox.confirm(
    message,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      showClose: false
    },
  ).then(() => {
    updateRoleStatus(roleId, enabled);
  }).catch(() => {
    const role = state.tableData.find(u => u.id === roleId);
    if (role) {
      role.enabled = !enabled;
    }
  });
};

const resetCreateForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const resetForm = () => {
  form.roleName = '';
  form.roleKey = '';
  form.remark = '';
  form.enabled = true;
  form.menuIds = [];
};

const openDialog = () => {
  isEditMode.value = false;
  resetCreateForm()
  resetForm()
  dialogFormVisible.value = true
}

const rules = reactive<FormRules>({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请输选择角色', trigger: 'blur' }
  ],
})

const cancel = () => {
  resetForm()
  dialogFormVisible.value = false
}

onMounted(() => {
  getTableData();
});
</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.role_manager') }}</p>
  </div>
  <div class="container-content">
    <el-row class="top-operate-content">
      <el-col :span="12">
        <el-button type="primary" @click="openDialog">
          <template #icon>
            <Icon name="icon_add_outlined" />
          </template>
          添加角色
        </el-button>
      </el-col>
      <el-col
        :span="12"
        class="right-filter"
      >
        <el-input
          ref="search"
          v-model="uname"
          :placeholder="t('user.search_role_placeholder')"
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
          key="roleName"
          prop="roleName"
          label="角色名称"
          resizable
          sortable
          show-overflow-tooltip
        />
        <el-table-column
          key="roleKey"
          prop="roleKey"
          label="角色编码"
          sortable
          resizable
          show-overflow-tooltip
        />
        <el-table-column
          key="enabled"
          prop="enabled"
          label="状态"
        >
          <template #default="scope">
            <el-switch
              v-model="scope.row.enabled"
              :active-value="true"
              :inactive-value="false"
              inactive-color="#DCDFE6"
              @change="confirmUpdateRoleStatus(scope.row.id, scope.row.enabled)"
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
            >
              <template #icon>
                <Icon name="icon_edit_outlined"></Icon>
              </template>
            </el-button>
            <el-button
              text
              v-permission="['dataset']"
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
      title="添加角色"
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" clearable type="textarea" :rows="3"/>
        </el-form-item>
        <el-form-item label="权限配置" prop="menuIds">
          <el-tree
            empty-text=""
            style="max-width: 600px"
          />
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
.info-table {
  height: calc(100% - 49px);
}
</style>
