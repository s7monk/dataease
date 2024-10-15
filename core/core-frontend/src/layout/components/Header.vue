<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { usePermissionStore } from '@/store/modules/permission'
import { isExternal } from '@/utils/validate'
import { formatRoute } from '@/router/establish'
import HeaderMenuItem from './HeaderMenuItem.vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import { Icon } from '@/components/icon-custom'
import { ElHeader, ElMenu } from 'element-plus-secondary'
import SystemCfg from './SystemCfg.vue'
import ToolboxCfg from './ToolboxCfg.vue'
import { useRouter, useRoute } from 'vue-router'
import AccountOperator from '@/layout/components/AccountOperator.vue'
import { isDesktop } from '@/utils/ModelUtil'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import AiComponent from '@/layout/components/AiComponent.vue'
import { findBaseParams } from '@/api/aiComponent'
import ExportExcel from '@/views/visualized/data/dataset/ExportExcel.vue'
import Copilot from '@/layout/components/Copilot.vue'
import { getActiveProjects } from '@/api/project'
import { useGnStoreWithOut } from '@/store/modules/gn'

const gnStore = useGnStoreWithOut()
const appearanceStore = useAppearanceStoreWithOut()
const { push } = useRouter()
const route = useRoute()
import { useCache } from '@/hooks/web/useCache'
const { wsCache } = useCache('localStorage')
const aiBaseUrl = ref('https://maxkb.fit2cloud.com/ui/chat/2ddd8b594ce09dbb?mode=embed')
const handleIconClick = () => {
  if (route.path === '/workbranch/index') return
  push('/workbranch/index')
}

const handleCopilotClick = () => {
  push('/copilot/index')
}

const desktop = isDesktop()
const activeIndex = computed(() => {
  if (route.path.includes('system')) {
    return '/system/user'
  }
  return route.path
})
const permissionStore = usePermissionStore()
const ExportExcelRef = ref()
const selectedProject = ref('')
const defaultProject  = ref(null);
const projects = ref([])
const handleSelectGn = (projectName) => {
  const selected = projects.value.find(project => project.name === projectName)
  if (selected) {
    selectedProject.value = selected.name
    gnStore.setGn(selected.gn)
  }
};

const getActiveProject = async () => {
  try {
    const response = await getActiveProjects()
    projects.value = response.data || []

    // 查找默认选中的项目
    const defaultSelectedProject = projects.value.find(project => project.default_selected === 1)

    if (defaultSelectedProject) {
      selectedProject.value = defaultSelectedProject.name
      defaultProject.value = defaultSelectedProject
      // 存入 gnStore
      gnStore.setGn(defaultSelectedProject.gn)
    } else if (projects.value.length > 0) {
      selectedProject.value = projects.value[0].name
      defaultProject.value = projects.value[0]
      gnStore.setGn(projects.value[0].gn)
    }
  } catch (error) {
    console.error('获取项目列表失败', error)
  }
}

const downloadClick = params => {
  ExportExcelRef.value.init(params)
}
const routers: any[] = formatRoute(permissionStore.getRoutersNotHidden as AppCustomRouteRecordRaw[])
const showSystem = ref(true)
const showToolbox = ref(true)
const showOverlay = ref(false)
const showOverlayCopilot = ref(true)
const handleSelect = (index: string) => {
  // 自定义事件
  if (isExternal(index)) {
    window.open(index, '_blank')
  } else {
    push(index)
  }
}
const initShowSystem = () => {
  showSystem.value = permissionStore.getRouters.some(route => route.path === '/system')
}
const initShowToolbox = () => {
  showToolbox.value = permissionStore.getRouters.some(route => route.path === '/toolbox')
}
const navigateBg = computed(() => appearanceStore.getNavigateBg)
const navigate = computed(() => appearanceStore.getNavigate)

const initAiBase = async () => {
  // const aiTipsCheck = wsCache.get('DE-AI-TIPS-CHECK')
  // if (aiTipsCheck === 'CHECKED') {
  //   showOverlay.value = false
  // } else {
  //   showOverlay.value = true
  // }
  await findBaseParams().then(rsp => {
    const params = rsp.data
    if (params && params['ai.baseUrl']) {
      aiBaseUrl.value = params['ai.baseUrl']
    } else {
      aiBaseUrl.value = null
    }
  })
}

const initCopilotBase = async () => {
  const aiCopilotCheck = wsCache.get('DE-COPILOT-TIPS-CHECK')
  if (aiCopilotCheck === 'CHECKED') {
    showOverlayCopilot.value = false
  } else {
    showOverlayCopilot.value = true
  }
}

const aiTipsConfirm = () => {
  wsCache.set('DE-AI-TIPS-CHECK', 'CHECKED')
  showOverlay.value = false
}

const copilotConfirm = () => {
  wsCache.set('DE-COPILOT-TIPS-CHECK', 'CHECKED')
  showOverlayCopilot.value = false
}

onMounted(() => {
  initShowSystem()
  initShowToolbox()
  initAiBase()
  initCopilotBase()
  getActiveProject()
  useEmitt({
    name: 'data-export-center',
    callback: function (params) {
      ExportExcelRef.value.init(params)
    }
  })
})
</script>

<template>
  <el-header class="header-flex" :class="{ 'header-light': navigateBg && navigateBg === 'light' }">
    <img class="logo" v-if="navigate" :src="navigate" alt="" />
    <span class="logo2">巴别时代</span>
<!--    <Icon
      style="cursor: pointer"
      v-else
      @click="handleIconClick"
      className="logo"
      name="logo"
    ></Icon>-->
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      :ellipsis="false"
      @select="handleSelect"
    >
      <HeaderMenuItem v-for="menu in routers" :key="menu.path" :menu="menu"></HeaderMenuItem>
    </el-menu>
    <div class="operate-setting" v-if="!desktop">
      <el-dropdown class="gn-container" trigger="click">
        <div style="cursor: pointer;">
          <span class="gn-span">{{ selectedProject }}</span>
          <el-icon class="el-icon-animate-h">
            <Icon name="icon_expand-down_filled" />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu >
            <el-dropdown-item
              v-for="project in projects"
              :key="project.id"
              @click="handleSelectGn(project.name)"
              :style="{ backgroundColor: selectedProject === project ? '#EAF0FF' : '', cursor: 'pointer' }"
            >
              {{ project.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-icon style="margin: 0 10px" class="ai-icon copilot-icon" v-if="!showOverlayCopilot">
        <Icon name="dv-ai" @click="handleCopilotClick" />
      </el-icon>
      <Copilot @confirm="copilotConfirm" v-if="showOverlayCopilot" class="copilot-icon-tips" />

      <el-tooltip effect="dark" content="数据导出中心" placement="bottom">
        <el-icon
          class="preview-download_icon"
          :class="navigateBg === 'light' && 'is-light-setting'"
        >
          <Icon name="dv-preview-download" @click="downloadClick" />
        </el-icon>
      </el-tooltip>

<!--      <ai-tips
        @confirm="aiTipsConfirm"
        v-if="showOverlay && appearanceStore.getShowAi"
        class="ai-icon-tips"
      />-->
      <ToolboxCfg v-if="showToolbox" />
      <SystemCfg v-if="showSystem" />
      <AccountOperator />
      <ai-component
        v-if="aiBaseUrl && appearanceStore.getShowAi"
        :base-url="aiBaseUrl"
      ></ai-component>
      <div v-if="showOverlay && appearanceStore.getShowAi" class="overlay"></div>
      <div v-if="showOverlayCopilot" class="overlay"></div>
    </div>
  </el-header>
  <ExportExcel ref="ExportExcelRef"></ExportExcel>
</template>

<style lang="less" scoped>
.gn-container {
  margin-right: 4px;
  .el-icon-animate-h {
    width: 12px;
    height: 12px;
    font-size: 14px !important;
  }
  .gn-span {
    font-family: '阿里巴巴普惠体 3.0 55 Regular L3';
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
  }
  .ed-icon {
    margin: 0 5px;
  }
}
.preview-download_icon {
  padding: 5px;
  height: 28px;
  width: 28px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  &:hover {
    background-color: #1e2738;
  }
  &.is-light-setting {
    &:hover {
      background-color: var(--ed-menu-hover-bg-color) !important;
    }
  }
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明黑色 */
  z-index: 10000;
}

.header-flex {
  margin-bottom: 0.5px;
  display: flex;
  align-items: center;
  height: 56px;
  background-color: #050e21;
  padding: 0 24px;
  .operate-setting {
    margin-left: auto;
    display: flex;
    align-items: center;
    &:focus {
      outline: none;
    }
  }

  .ed-menu {
    background-color: #050e21;
    height: 56px;
  }

  .ed-menu--horizontal {
    border: none;
    .ed-menu-item,
    :deep(.ed-sub-menu__title) {
      color: rgba(255, 255, 255, 0.8);
      line-height: 50px;
      border-bottom: none;

      &.is-active {
        border-bottom: none;
        color: #ffffff !important;
        background-color: var(--ed-color-primary);
      }
    }

    > .is-active {
      :deep(.ed-sub-menu__title) {
        color: #ffffff !important;
        background-color: var(--ed-color-primary);
      }
    }

    .ed-menu-item:not(.is-disabled):hover,
    :deep(.ed-sub-menu__title):not(.is-disabled):hover {
      color: #ffffffcc;
      background: #ffffff1a;
    }
  }
}

.header-light {
  background-color: #ffffff !important;
  box-shadow: 0px 0.5px 0px 0px #1f232926 !important;
  .ed-menu {
    background-color: #ffffff !important;
  }
  .ed-menu--horizontal {
    .ed-menu-item {
      color: var(--ed-color-black) !important;
    }
    :deep(.ed-sub-menu__title) {
      color: var(--ed-color-black) !important;
    }
    .ed-menu-item:not(.is-disabled):hover,
    :deep(.ed-sub-menu__title):not(.is-disabled):hover {
      color: #1f2329;
      background: #1f23291a;
    }
  }

  .logo {
    color: #3371ff !important;
  }
}

.logo {
  width: 134px;
  height: 34px;
  margin-right: 48px;
  color: #ffffff;
}

.logo2 {
  font-size: 24px; /* 设置字体大小 */
  font-weight: bold; /* 字体加粗 */
  color: #ffffff; /* 字体颜色设置为白色，以便在黑色背景上突出显示 */
  cursor: pointer; /* 鼠标悬停时显示指针形状 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); /* 增强文字阴影效果，使文字更突出 */
  padding: 10px 60px 10px 0; /* 调整内边距 */
  border-radius: 5px; /* 边角圆滑处理 */
  display: inline-block; /* 使元素像块级元素一样显示，但仍然在行内 */
  font-style: italic; /* 斜体 */
  transition: all 0.3s ease; /* 添加过渡动画，使鼠标悬停和其他交互更平滑 */
}

.logo2:hover {
  transform: scale(1.05); /* 鼠标悬停时轻微放大 */
  text-shadow: 2px 2px 6px rgba(255, 255, 255, 0.9); /* 鼠标悬停时增强文字阴影，增加亮度对比 */
}
</style>

<style lang="less">
.header-flex {
  .operate-setting {
    .ed-icon {
      cursor: pointer;
      color: rgba(255, 255, 255, 0.8);
      font-size: 18px;
    }
  }
}
.header-light {
  .operate-setting {
    .ed-icon {
      color: var(--ed-color-black) !important;
    }
  }
}

.ai-icon {
  font-size: 24px !important;
}

.ai-icon-tips,
.copilot-icon-tips {
  font-size: 24px !important;
  z-index: 10001;
}
</style>
