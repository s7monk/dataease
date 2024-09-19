<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
const { t } = useI18n()
const activeTab = ref('user')
const activeData = ref({});
const filterUser = ref('');

const userData = ref({
  docs_admin: { read: true, write: false, delete: false },
  docs_demo: { read: true, write: true, delete: false }
});

const roleData = ref({
  管理员: { read: true, write: true, delete: true },
  编辑者: { read: true, write: false, delete: false }
});

const handleTabClick = (tab) => {
  activeTab.value = tab.props.name;
  activeData.value = {};
};

const handleMenuSelect = (index) => {
  if (activeTab.value === 'user') {
    activeData.value = userData.value[index] || {};
  } else {
    activeData.value = roleData.value[index] || {};
  }
};

</script>

<template>
  <el-container class="user-auth-container">
    <el-aside class="user-list">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane name="user" label="用户">
          <div class="left-container">
            <el-input class="user-search-input" placeholder="搜索" v-model="filterUser" />
            <el-menu class="user-menu" @select="handleMenuSelect">
              <el-menu-item v-for="(value, key) in userData" :key="key" :index="key">
                {{ key }}
              </el-menu-item>
            </el-menu>
          </div>
        </el-tab-pane>
        <el-tab-pane name="role" label="角色">
          <el-input placeholder="搜索" v-model="filterUser" />
          <el-menu @select="handleMenuSelect">
            <el-menu-item v-for="(value, key) in userData" :key="key" :index="key">
              {{ key }}
            </el-menu-item>
          </el-menu>
        </el-tab-pane>
      </el-tabs>
    </el-aside>
    <el-main class="auth-config">
      <el-form>
        <el-form-item label="读取权限">
          <el-checkbox v-model="activeData.read" :disabled="!Object.keys(activeData).length">读取</el-checkbox>
        </el-form-item>
        <el-form-item label="写入权限">
          <el-checkbox v-model="activeData.write" :disabled="!Object.keys(activeData).length">写入</el-checkbox>
        </el-form-item>
        <el-form-item label="删除权限">
          <el-checkbox v-model="activeData.delete" :disabled="!Object.keys(activeData).length">删除</el-checkbox>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<style lang="less" scoped>
.ed-tabs--bottom .ed-tabs__item.is-bottom:nth-child(2), .ed-tabs--bottom .ed-tabs__item.is-top:nth-child(2), .ed-tabs--top .ed-tabs__item.is-bottom:nth-child(2), .ed-tabs--top .ed-tabs__item.is-top:nth-child(2) {
  padding-left: 20px;
}
.user-auth-container {
  width: 100%;
  height: 100%;

  .user-list {
    width: 260px;
    overflow: auto;
    border-right: 1px solid #ebeef5;

    .left-container {
      padding: 12px 20px 16px 20px;
      .user-search-input {
        width: 220px
      }
      .user-menu {
        border: none;
      }
    }
  }

  .auth-config {
    flex-grow: 1;
    padding: 20px;
  }
}
</style>
