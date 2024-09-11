<script lang="ts" setup>
import {reactive, ref, computed, onMounted, watch, toRefs} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import GridTable from "@/components/grid-table/src/GridTable.vue";
import {CustomPassword} from "@/components/custom-password";
import {userListApi} from "@/api/user";
import dayjs from 'dayjs'
import { debounce } from 'lodash';
import { FormRules } from 'element-plus-secondary'
const { t } = useI18n()

const dialogFormVisible = ref(false)
const uname = ref("")
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
  username: string
  nickname: string
  password: string
  mobile?: string
  email: string
  enabled: true
  roleIds?: []
}

const form = reactive<UserDTO>({
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
  ]
})


/*const handleSubmit = () => {
  const formEl = ref(null);

  formEl.value.validate((valid) => {
    if (valid) {
      userCreateApi(form).then(response => {
        if (response.success) {
          ElMessage.success('用户添加成功');
          // 重置表单或执行其他操作
        } else {
          ElMessage.error('添加失败: ' + response.message);
        }
      }).catch(error => {
        ElMessage.error('请求错误: ' + error.message);
      });
    } else {
      ElMessage.error('请检查输入');
      return false;
    }
  });
};*/

const getTableData = () => {
  const params = {
    username: uname.value,
    nickname: uname.value,
    pageNum: state.paginationConfig.currentPage,
    pageSize: state.paginationConfig.pageSize
  }

  userListApi(params).then(res => {
    state.tableData = res.data.data.map(item => {
      const {username, nickname, mobile, email, enabled, createTime} = item
      return {
        username: username,
        nickname: nickname,
        mobile: mobile,
        email: email,
        enabled: enabled,
        createTime: dayjs(createTime).format('YYYY-MM-DD HH:mm:ss')
      }
    })
    state.paginationConfig.total = res.data.total
    console.log(res.data)
  })
}

const debouncedGetTableData  = debounce(getTableData, 300);

watch(uname, () => {
  debouncedGetTableData();
});

onMounted(getTableData)
</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.user_manager') }}</p>
  </div>
  <div class="container-content">
    <el-row class="top-operate-content">
      <el-col :span="12">
<!--        <el-button type="primary" @click="dialogFormVisible = true">-->
        <el-button type="primary" @click="dialogFormVisible = true">
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
      title="添加用户"
      v-model="dialogFormVisible"
      width="560px"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        style="max-width: 560px"
        label-position="left"
        require-asterisk-position="right"
        label-width="auto">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <CustomPassword
            v-model="form.password"
            placeholder="请输入密码"
            show-password
            maxlength="30"
            show-word-limit
            autocomplete="new-password"
          />
<!--          <el-input show-password v-model="form.password" placeholder="请输入密码" clearable />-->
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roles" placeholder="请选择角色" style="width: 100%">
            <el-option label="Admin" value="1" />
            <el-option label="Common" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogFormVisible = false">
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
