<script lang="ts" setup>
import {reactive, ref} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import GridTable from "@/components/grid-table/src/GridTable.vue";
const { t } = useI18n()

const dialogFormVisible = ref(false)
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
  tableData: [
    {username: 'admin', nickname:  '超级管理员', phone: 17710766017, email: '17710766017@163.com', enabled: 1, createTime: '2024-09-05 17:11:23'},
    {username: 'common', nickname:  '普通用户', phone: 17710766017, email: '17710766017@163.com', enabled: 1, createTime: '2024-09-05 17:11:23'},
  ],
  curTypeList: [],
  tableColumn: []
})

const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})
</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.user_manager') }}</p>
  </div>
  <div class="container-content">
    <el-row class="top-operate-content">
      <el-col :span="12">
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
          v-model="nickName"
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
        :table-data="state.tableData"
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
          key="phone"
          prop="phone"
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
          key="state"
          prop="state"
          label="状态"
          width="80"
        >
          <template #default="scope">
            <el-switch
              v-model="scope.row.enabled"
              :active-value="1"
              :inactive-value="0"
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
      width="600px"
    >
      <el-form :model="form">
        <el-form-item label="用户名">
          <el-input v-model="form.name" placeholder="请输入用户名" clearable />
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
