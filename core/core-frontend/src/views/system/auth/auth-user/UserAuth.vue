<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {ElDivider} from "element-plus-secondary";
const { t } = useI18n()
const activeTab = ref('user')
const activeResourceTab = ref('resourceTab')
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
    <div class="right-container">
      <el-tabs v-model="activeResourceTab">
        <el-tab-pane name="resourceTab" label="资源权限">
          <el-container class="right-main-container">
            <el-aside class="right-main-aside">
              <div class="right-main-aside-menu">
                <el-menu class="right-menu" @select="handleMenuSelect">
                  <el-menu-item index="1">数据看板</el-menu-item>
                  <el-menu-item index="2">数据大屏</el-menu-item>
                  <el-menu-item index="3">数据集</el-menu-item>
                  <el-menu-item index="4">数据源</el-menu-item>
                </el-menu>
              </div>
            </el-aside>
          </el-container>
        </el-tab-pane>
      </el-tabs>
      <el-button type="info" class="save-button" plain>保存</el-button>
    </div>
  </el-container>
</template>

<style lang="less" scoped>
::v-deep .ed-tabs__nav.is-top .ed-tabs__item{
  padding-left: 30px;
}
::v-deep .right-main-aside-menu .ed-menu {
  padding:  0 20px 0 0;
}

::v-deep .right-main-aside-menu .ed-menu-item {
  border-radius: 4px; // 可选，增加圆角效果
}

::v-deep .right-container .ed-tabs  {
  height: calc(100vh - 181px);;
}

::v-deep .right-container .ed-tabs__content  {
  height: calc(100vh - 225px);
}

::v-deep .right-container .ed-tab-pane  {
  height: 100%;
}

::v-deep .right-container .ed-main  {
  height: 100%;
}

.user-auth-container {
  width: 100%;
  height: 100%;

  .user-list {
    width: 260px;
    overflow: auto;
    border-right: 1.2px solid #d1d4db;

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
    height: 100%;
  }

  .right-container {
    width: 100%;
    position: relative;

    .save-button {
      height: 30px;
      position: absolute;
      right: 20px;
      top: 8px;
      z-index: 100; // 确保按钮在最前面
    }

    .right-main-container {
      width: 100%;
      height: 100%;

      .right-main-aside {
        width: 160px;
        .right-main-aside-menu {
          height: 100%;
          border-right: 1.2px solid #d1d4db;
          .right-menu {
            padding: 20px;
            border: none;
          }
        }
      }
    }
  }
}
</style>
