<script lang="ts" setup>
import {ref, reactive, computed, watch, onMounted} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import GridTable from "@/components/grid-table/src/GridTable.vue";
import {ElMessage, ElMessageBox, FormRules} from "element-plus-secondary";
import {roleListApi, roleStatusUpdateApi, roleMenuApi, roleCreateApi} from "@/api/user";
import dayjs from "dayjs";
import {debounce} from "lodash";

const { t } = useI18n()
const uname = ref("")
const dialogFormVisible = ref(false)
const isEditMode = ref(false);
const formRef = ref(null)
const menuOptions = ref([])
const menuTreeRef = ref(null);
const menuExpand = ref(false)
const menuNodeAll = ref(false)
const menuCheckStrictly = ref(true)

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
  menuExpand.value = false;
  menuNodeAll.value = false;
  menuCheckStrictly.value = true;
  if (menuTreeRef.value) {
    menuTreeRef.value.setCheckedKeys([]);
  }
  handleCheckedTreeExpand(menuExpand.value);
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
})

const cancel = () => {
  resetForm()
  dialogFormVisible.value = false
}

const getMenuTreeselect = () => {
  const params = {
    pageNum: 1,
    pageSize: 10000000000
  }

  roleMenuApi(params).then(response => {
    const filteredMenuOptions = response.data.data.filter(item => {
      return !['dataset-form', 'datasource-form', 'sys-setting'].includes(item.label);
    });
    menuOptions.value = filteredMenuOptions;
  });
}

const renderContent = (h, { node, data }) => {
  return h('span', t(`roleKey.${data.label}`));
};

const handleCheckedTreeExpand = (value) => {
  if (!menuTreeRef.value) return;

  const nodesMap = menuTreeRef.value.store.nodesMap;
  menuOptions.value.forEach(item => {
    if (nodesMap[item.id]) {
      nodesMap[item.id].expanded = value;
    }
  });
};

const handleCheckedTreeNodeAll = (value) => {
  menuTreeRef.value.setCheckedNodes(value ? menuOptions.value : []);
};

const handleCheckedTreeConnect = (value) => {
  menuCheckStrictly.value = value;
};

const getMenuAllCheckedKeys = () => {
  if (!menuTreeRef.value) {
    return [];
  }
  let checkedKeys = menuTreeRef.value.getCheckedKeys()
  let halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys();
  return [...checkedKeys, ...halfCheckedKeys];
}

const handleSubmit = async () => {
  if (!formRef.value) {
    ElMessage.error('表单引用不存在');
    return;
  }

  try {
    await formRef.value.validate(async (valid) => {
      if (valid) {
        const checkedKeys = getMenuAllCheckedKeys();

        form.menuIds = checkedKeys;

        const response = await roleCreateApi(form);
        if (response.data.code === 200) {
          ElMessage.success('角色创建成功');
          dialogFormVisible.value = false;
          getTableData();
        } else {
          ElMessage.error('角色创建失败: ' + response.data.message);
        }
      } else {
        ElMessage.error('请填写正确的角色信息');
        return false;
      }
    })
  } catch (error) {
    if (error instanceof Error) {
      console.error('表单校验失败:', error);
      ElMessage.error('表单校验失败: ' + error.message);
    }
  }
};

onMounted(() => {
  getTableData();
  getMenuTreeselect()
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
        <el-form-item label="菜单权限" prop="menuIds">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
          <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect($event)">父子联动</el-checkbox>
          <el-tree
            empty-text=""
            ref="menuTreeRef"
            class="tree-border"
            style="max-width: 600px"
            show-checkbox
            node-key="id"
            :check-strictly="!menuCheckStrictly"
            :data="menuOptions"
            :render-content="renderContent"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
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

.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF none;
  border-radius: 4px;
  width: 100%;
}
</style>
