<script lang="ts" setup>
import {ref, computed, reactive, watch, onMounted, nextTick} from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import {Icon} from "@/components/icon-custom";
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import EmptyBackground from "@/components/empty-background/src/EmptyBackground.vue";
import {ElIcon} from "element-plus-secondary";
import {roleListApi, userListApi} from "@/api/user";
import { getDashboardsByUserId } from "@/api/auth";
import dayjs from "dayjs";
import {debounce} from "lodash";
const { t } = useI18n()
const activeTab = ref('user')
const activeResourceTab = ref('resourceTab')
const activeData = ref({});
const filterUser = ref('');
const filterRole = ref('');
const filterResource = ref('');
const activeMenuIndex = ref('1');
const activeRoleMenuIndex = ref("1");
const activeResourceIndex = ref('1');
const resourceTable = ref(null);
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
  activeMenuIndex.value = index
};

const handleRoleMenuSelect = (index: string) => {
  const roleId = parseInt(index, 10);
  console.log(roleId)
};

const treeProps = reactive({
  checkStrictly: false,
})

const state = reactive({
  userTableData: [],
  roleTableData: [],
  dashboardsWithUserTableData: [],
  originalDashboards: [],
  expandedRowKeys: new Set<string>(),
  highlightedRowKeys: new Set<string>(),
  selectedRowKeys: new Set<string>(),
})

const getDashboardsWithUserTableData = () => {
  getDashboardsByUserId(activeMenuIndex.value, '').then(res => {
    //state.dashboardsWithUserTableData = res.data.map(item => transformItem(item));
    state.originalDashboards = res.data.map(transformItem);
    state.dashboardsWithUserTableData = state.originalDashboards;
  });
};

const debouncedGetDashboardsWithUserTableData  = debounce(getDashboardsWithUserTableData, 300);

/*watch(filterResource, () => {
  debouncedGetDashboardsWithUserTableData();
});*/

const transformItem = (item) => {
  const { resourceId, resourceName, isSelect, isManage, isShare, isExport, isAuth, leaf, children } = item;
  return {
    id: resourceId,
    name: resourceName,
    select: isSelect === 1,
    manage: isManage === 1,
    share: isShare === 1,
    export: isExport === 1,
    auth: isAuth === 1,
    leaf: leaf === 1,
    children: children ? children.map(child => transformItem(child)) : [],
  };
};

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

watch(activeMenuIndex, () => {
  getDashboardsWithUserTableData();
});


watch(filterRole, () => {
  debouncedGetRoleTableData();
});

const searchResource = () => {
  if (!filterResource.value) {
    state.dashboardsWithUserTableData = state.originalDashboards;
    state.expandedRowKeys.clear();
    state.highlightedRowKeys.clear();
    state.selectedRowKeys.clear();
    return;
  }

  const searchTerm = filterResource.value.toLowerCase();
  const searchResults = [];

  const searchTree = (nodes, parentPath = []) => {
    for (const node of nodes) {
      const currentPath = [...parentPath, node];
      if (node.name.toLowerCase().includes(searchTerm)) {
        searchResults.push(currentPath);
      }
      if (node.children && node.children.length) {
        searchTree(node.children, currentPath);
      }
    }
  };

  searchTree(state.originalDashboards);

  if (searchResults.length > 0) {
    const expandedResults = expandAndHighlightSearchResults(searchResults, searchTerm);
    state.dashboardsWithUserTableData = expandedResults;
  } else {
    state.dashboardsWithUserTableData = [];
  }

  console.log(state.dashboardsWithUserTableData)
  console.log('Expanded Row Keys:', Array.from(state.expandedRowKeys));
};

const expandAndHighlightSearchResults = (searchResults, searchTerm) => {
  const expandedResults = [];
  const addedNodeIds = new Set<string>();
  const highlightedNodeIds = new Set<string>();

  const addChildren = (node, targetArray) => {
    if (addedNodeIds.has(node.id)) return;
    const newNode = { ...node, children: [] };
    targetArray.push(newNode);
    addedNodeIds.add(node.id);
    if (node.children && node.children.length) {
      for (const child of node.children) {
        addChildren(child, newNode.children);
      }
    }
  };

  let selectedRow = null;

  for (const path of searchResults) {
    let currentLevel = expandedResults;
    for (const node of path) {
      if (!addedNodeIds.has(node.id)) {
        const newNode = { ...node, children: [] };
        currentLevel.push(newNode);
        addedNodeIds.add(node.id);
        currentLevel = newNode.children;
        if (node.children && node.children.length) {
          for (const child of node.children) {
            addChildren(child, newNode.children);
          }
        }
      } else {
        const existingNode = currentLevel.find(n => n.id === node.id);
        currentLevel = existingNode.children;
      }
      if (node.name.toLowerCase().includes(searchTerm)) {
        if (node.leaf) {
          highlightedNodeIds.add(node.id);
          state.expandedRowKeys.add(node.id);
          selectedRow = node;
          for (const parentNode of path) {
            state.expandedRowKeys.add(parentNode.id);
          }
        } else {
          highlightedNodeIds.add(node.id);
        }

      }
      // state.expandedRowKeys.add(node.id);

      if (selectedRow) {
        nextTick(() => {
          if (resourceTable.value) {
            resourceTable.value.setCurrentRow(selectedRow);
          }
        });
      }
    }
  }

  state.highlightedRowKeys = highlightedNodeIds;
  return expandedResults;
};

/*const expandAndHighlightSearchResults = (searchResults) => {
  const expandedResults = [];
  const addedNodeIds = new Set<string>();
  const highlightedNodeIds = new Set<string>();

  const addNodeWithChildren = (node, targetArray) => {
    if (addedNodeIds.has(node.id)) return;
    const newNode = { ...node, children: [] };
    targetArray.push(newNode);
    addedNodeIds.add(node.id);
    if (node.children && node.children.length) {
      for (const child of node.children) {
        addNodeWithChildren(child, newNode.children);
      }
    }
  };

  const addNodeWithoutSiblings = (node, targetArray) => {
    if (addedNodeIds.has(node.id)) return;
    const newNode = { ...node, children: [] };
    targetArray.push(newNode);
    addedNodeIds.add(node.id);
    if (node.children && node.children.length) {
      for (const child of node.children) {
        addNodeWithoutSiblings(child, newNode.children);
      }
    }
  };

  let selectedRow = null;

  for (const path of searchResults) {
    let currentLevel = expandedResults;
    const isParentMatch = path[path.length - 1].leaf === false; // 判断最后一个节点是否是父节点

    for (let i = 0; i < path.length; i++) {
      const node = path[i];
      if (!addedNodeIds.has(node.id)) {
        const newNode = { ...node, children: [] };
        currentLevel.push(newNode);
        addedNodeIds.add(node.id);
        currentLevel = newNode.children;

        if (isParentMatch && i === path.length - 1) {
          // 如果是父节点匹配，添加父节点及其所有子节点
          addNodeWithChildren(node, newNode.children);
          highlightedNodeIds.add(node.id);
        } else if (!isParentMatch && i === path.length - 1) {
          // 如果是子节点匹配，添加子节点及其父节点，并展开父节点
          highlightedNodeIds.add(node.id);
          state.expandedRowKeys.add(node.id);
          selectedRow = node;
          for (const parentNode of path.slice(0, -1)) {
            state.expandedRowKeys.add(parentNode.id);
          }
        } else if (!isParentMatch) {
          // 如果是路径中的父节点，添加父节点但不包括兄弟节点
          addNodeWithoutSiblings(node, newNode.children);
        }
      } else {
        const existingNode = currentLevel.find(n => n.id === node.id);
        currentLevel = existingNode.children;
      }
    }
  }

  if (selectedRow) {
    nextTick(() => {
      if (resourceTable.value) {
        resourceTable.value.setCurrentRow(selectedRow);
      }
    });
  }

  state.highlightedRowKeys = highlightedNodeIds;
  return expandedResults;
};*/

watch(filterResource, debounce(searchResource, 300));

const rowClassName = ({ row }) => {
  return state.highlightedRowKeys.has(row.id) ? 'highlight-row' : '';
};

onMounted(() => {
  getUserTableData();
  getRoleTableData();
  getDashboardsWithUserTableData();
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
              <el-input class="user-search-input" placeholder="搜索" v-model="filterResource" clearable>
                <template #prefix>
                  <el-icon>
                    <Icon name="icon_search-outline_outlined" />
                  </el-icon>
                </template>
              </el-input>
              <el-table
                header-cell-class-name="header-cell"
                :data="state.dashboardsWithUserTableData"
                class="resource-table"
                row-key="id"
                height="calc(100vh - 306px)"
                :highlight-current-row="true"
                :expand-row-keys="Array.from(state.expandedRowKeys)"
                :row-class-name="rowClassName"
                ref="resourceTable"
              >
                <el-table-column prop="id" key="id" label="资源名称">
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
                <el-table-column width="80" prop="select" key="select" label="查看" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.select" size="default" />
                  </template>
                </el-table-column>
                <el-table-column width="80" prop="manage" key="manage" label="管理" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.manage"  size="default" />
                  </template>
                </el-table-column>
                <el-table-column  width="80" prop="share" key="share" label="分享" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.share" size="default" />
                  </template>
                </el-table-column>
                <el-table-column width="80" prop="export" key="export" label="导出" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.export" size="default" />
                  </template>
                </el-table-column>
                <el-table-column width="80" fixed="right"  prop="auth" key="auth" label="授权" align="center">
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.auth" size="default" />
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

<style lang="less" scoped>父节点匹配时：返回父节点及其所有子节点，但不展开父节点。
子节点匹配时：返回匹配的子节点及其父节点，且展开父节点，并且不返回未匹配的兄弟节点。
.highlight-row {
  background-color: #ffffcc !important;
}
.right-container :deep(.ed-tabs__active-bar) {
  width: 63px !important;
  left: 66% !important;
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
