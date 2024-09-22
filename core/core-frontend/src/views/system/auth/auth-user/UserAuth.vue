<script lang="ts" setup>
import {ref, computed, reactive, watch, onMounted} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
const { t } = useI18n()
const activeTab = ref('user')
const activeResourceTab = ref('resourceTab')
const activeData = ref({});
const filterUser = ref('');
const filterRole = ref('');
const activeMenuIndex = ref("1");
const activeRoleMenuIndex = ref("1");
const activeResourceIndex = ref('1');
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import EmptyBackground from "@/components/empty-background/src/EmptyBackground.vue";
import {ElIcon} from "element-plus-secondary";
import {roleListApi, userListApi} from "@/api/user";
import dayjs from "dayjs";
import {debounce} from "lodash";
const appearanceStore = useAppearanceStoreWithOut()
const tempColor = computed(() => {
  return {
    '--temp-color':
      (appearanceStore.themeColor === 'custom' ? appearanceStore.customColor : '#3370FF') + '1A'
  }
})

const handleTabClick = (tab) => {
  activeTab.value = tab.props.name;
  activeData.value = {};
};

const handleUserMenuSelect = (index: string) => {
  const userId = parseInt(index, 10);
};

const handleRoleMenuSelect = (index: string) => {
  const roleId = parseInt(index, 10);
  console.log(roleId)
};

const state = reactive({
  userTableData: [],
  roleTableData: [],
  tableData: [
    {
      id: 1,
      name: "数据看板",
      select: true,
      manage: true,
      share: true,
      export: false,
      leaf: false,
      children: [
        {
          id: 2,
          name: "演示看板",
          select: true,
          manage: true,
          share: true,
          export: false,
          leaf: false,
          children: [
            {
              id: 3,
              name: "用户看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 4,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 5,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 6,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 7,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 8,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 9,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
            {
              id: 10,
              name: "角色看板",
              select: true,
              manage: true,
              share: true,
              export: false,
              leaf: true,
            },
          ],
        },
      ],
    }
  ],
})

const getUserTableData = () => {
  const params = {
    nickname: filterUser.value,
    pageNum: 1,
    pageSize: 999999999
  }

  userListApi(params).then(res => {
    state.userTableData = res.data.data.map(item => {
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
  })
}

const debouncedGetUserTableData  = debounce(getUserTableData, 300);

watch(filterUser, () => {
  debouncedGetUserTableData();
});

const getRoleTableData = () => {
  const params = {
    roleName: filterRole.value,
    pageNum: 1,
    pageSize: 99999999
  }

  roleListApi(params).then(res => {
    state.roleTableData = res.data.data.map(item => {
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
  })
}

const debouncedGetRoleTableData  = debounce(getRoleTableData, 300);

watch(filterRole, () => {
  debouncedGetRoleTableData();
});

onMounted(() => {
  getUserTableData();
  getRoleTableData();
});
</script>

<template>
  <el-container class="user-auth-container">
    <el-aside class="user-list">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane name="user" label="用户">
          <div class="left-container">
            <el-input class="user-search-input" placeholder="搜索" v-model="filterUser" clearable>
              <template #prefix>
                <el-icon>
                  <Icon name="icon_search-outline_outlined" />
                </el-icon>
              </template>
            </el-input>
            <el-scrollbar height="calc(100vh - 286px)">
              <el-menu class="user-menu" :style="tempColor" :default-active="activeMenuIndex" @select="handleUserMenuSelect">
                <el-menu-item v-for="value in state.userTableData" :key="value.id" :index="value.id.toString()">
                  {{ value.nickname }}
                </el-menu-item>
              </el-menu>
            </el-scrollbar>
          </div>
        </el-tab-pane>
        <el-tab-pane name="role" label="角色">
          <div class="left-container">
            <el-input class="user-search-input" placeholder="搜索" v-model="filterRole" clearable>
              <template #prefix>
                <el-icon>
                  <Icon name="icon_search-outline_outlined" />
                </el-icon>
              </template>
            </el-input>
            <el-scrollbar height="calc(100vh - 286px)">
              <el-menu class="user-menu" :style="tempColor" @select="handleRoleMenuSelect" :default-active="activeRoleMenuIndex">
                <el-menu-item v-for="value in state.roleTableData" :key="value.id" :index="value.id.toString()">
                  {{ value.roleName }}
                </el-menu-item>
              </el-menu>
            </el-scrollbar>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-aside>
    <div class="right-container">
      <el-tabs v-model="activeResourceTab">
        <el-tab-pane name="resourceTab" label="资源权限">
          <el-container class="right-main-container">
            <el-aside class="right-main-aside">
              <div class="right-main-aside-menu">
                <el-scrollbar height="calc(100vh - 226px)">
                  <el-menu class="right-menu" :style="tempColor"  :default-active="activeResourceIndex" @select="handleMenuSelect">
                    <el-menu-item index="1">数据看板</el-menu-item>
                    <el-menu-item index="2">数据大屏</el-menu-item>
                    <el-menu-item index="3">数据集</el-menu-item>
                    <el-menu-item index="4">数据源</el-menu-item>
                  </el-menu>
                </el-scrollbar>
              </div>
            </el-aside>
            <el-main class="right-el-main">
              <el-input class="user-search-input" placeholder="搜索" v-model="filterUser" >
                <template #prefix>
                  <el-icon>
                    <Icon name="icon_search-outline_outlined" />
                  </el-icon>
                </template>
              </el-input>
              <el-table
                header-cell-class-name="header-cell"
                :data="state.tableData"
                class="resource-table"
                row-key="id"
                height="calc(100vh - 304px)"
              >
                <el-table-column prop="name" key="name" label="资源名称">
                  <template #default="scope">
                    <el-icon class="resource-tree-first-cell-icon" v-if="scope.row.leaf">
                      <Icon name="dv-dashboard-spine"/>
                    </el-icon>
                    <el-icon class="resource-tree-first-cell-icon" v-else-if="!scope.row.leaf">
                      <Icon name="dv-folder" />
                    </el-icon>
                    <span class="resource-tree-first-cell-span">{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column width="100" prop="select" key="select" label="查看" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.select" size="default" />
                  </template>
                </el-table-column>
                <el-table-column width="100" prop="manage" key="manage" label="管理" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.manage"  size="default" />
                  </template>
                </el-table-column>
                <el-table-column  width="100" prop="share" key="share" label="分享" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.share" size="default" />
                  </template>
                </el-table-column>
                <el-table-column width="100" fixed="right"  prop="export" key="export" label="导出" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.export" size="default" />
                  </template>
                </el-table-column>
                <template #empty>
                  <empty-background
                    description="暂无数据"
                    img-type="noneWhite"
                  />
                </template>
              </el-table>
            </el-main>
          </el-container>
        </el-tab-pane>
      </el-tabs>
      <el-button type="info" class="save-button" plain>保存</el-button>
    </div>
  </el-container>
</template>

<style lang="less" scoped>
.right-container :deep(.ed-tabs__active-bar) {
  width: 64px !important;
  left: 67% !important;
  transform: translateX(-50%) !important;
}

.ed-menu {
  border: none;
  .ed-menu-item:not(.is-active) {
    &:hover {
      background-color: #1f23291a !important;
    }
  }
  .is-active:not(.ed-sub-menu) {
    background-color: var(--temp-color);
  }
  :deep(.ed-sub-menu) {
    margin: 0;
    .ed-sub-menu__title {
      &:hover {
        background-color: #1f23291a;
      }
    }
    .ed-menu-item:not(.is-active) {
      &:hover {
        background-color: #1f23291a !important;
      }
    }
    ul.ed-menu {
      li.ed-menu-item {
        i {
          width: 4px !important;
        }
      }
    }
  }
  :deep(.ed-sub-menu.is-active) {
    .ed-sub-menu__title {
      color: var(--ed-color-primary);
    }
    .is-active {
      background-color: var(--temp-color);
    }
  }
}
:deep(.ed-tabs__nav.is-top  .ed-tabs__item) {
  padding-left: 30px;
}
.right-main-aside-menu :deep(.ed-menu) {
  padding:  0 20px 0 0;
}

.right-main-aside-menu :deep(.ed-menu-item) {
  border-radius: 4px;
}

.left-container :deep(.ed-menu-item) {
  border-radius: 4px;
}

.right-container :deep(.ed-tabs)  {
  height: calc(100vh - 181px);;
}

.right-container  :deep(.ed-tabs__content)  {
  height: calc(100vh - 225px);
}

.right-container  :deep(.ed-tab-pane)  {
  height: 100%;
}

.right-container  :deep(.ed-main) {
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
        margin-top: 16px;
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
    width: calc(100% - 260px);
    height: 100%;
    position: relative;

    .save-button {
      height: 30px;
      position: absolute;
      right: 20px;
      top: 8px;
      z-index: 100;
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
    .resource-table {
      width: 100%;
      margin-top: 16px;

      .resource-tree-first-cell-icon {
        font-size: 18px;
        position: relative;
        top: 3.3px;
      }

      .resource-tree-first-cell-span {
        margin-left: 10px;
      }
    }
  }

  .right-el-main {
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
}
</style>
