<script lang="ts" setup>
import { computed } from 'vue'
import { ElHeader } from 'element-plus-secondary'
import { useRouter } from 'vue-router'
import AccountOperator from '@/layout/components/AccountOperator.vue'
import { propTypes } from '@/utils/propTypes'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
const appearanceStore = useAppearanceStoreWithOut()
const { push } = useRouter()
const props = defineProps({
  title: propTypes.string.def('系统设置')
})
const backToMain = () => {
  push('/workbranch/index')
}
const navigateBg = computed(() => appearanceStore.getNavigateBg)
const navigate = computed(() => appearanceStore.getNavigate)
</script>

<template>
  <el-header
    class="header-flex system-header"
    :class="{ 'header-light': navigateBg && navigateBg === 'light' }"
  >
    <img class="logo" v-if="navigate" :src="navigate" alt="" />
    <span class="logo2">巴别时代</span>
<!--    <Icon class="de-logo" v-else className="logo" name="logo"></Icon>-->
    <el-divider direction="vertical" />
    <span class="system">{{ props.title || '系统设置' }}</span>
    <div class="operate-setting">
      <span @click="backToMain" class="work-bar flex-align-center">
        <el-icon>
          <Icon name="icon_left_outlined"></Icon>
        </el-icon>
        <span class="work">返回工作台</span>
      </span>

      <AccountOperator />
    </div>
  </el-header>
</template>

<style lang="less" scoped>
.system-header {
  font-family: '阿里巴巴普惠体 3.0 55 Regular L3';

  .logo {
    width: 134px;
    height: 34px;
  }

  .de-logo {
    color: #ffffff;
  }

  .ed-divider {
    margin: 0 24px;
    border-color: rgba(255, 255, 255, 0.3);
  }
  .system {
    color: #fff;
    font-size: 16px;
    font-style: normal;
    font-weight: 500;
    line-height: 24px;
  }

  .work-bar {
    margin-right: 20px;
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 22px;
    cursor: pointer;
    .ed-icon {
      margin-right: 4px;
      font-size: 16px;
    }
  }

  .avatar {
    margin: 0 -7px 0 20px !important;
  }
}
.header-light {
  background-color: #ffffff !important;
  box-shadow: 0px 0.5px 0px 0px #1f232926 !important;
  :deep(.work-bar) {
    color: var(--ed-color-black) !important;
  }
  .ed-divider {
    border-color: #1f232926 !important;
  }

  .system {
    color: #000 !important;
  }
  .de-logo {
    color: #3371ff !important;
  }
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
