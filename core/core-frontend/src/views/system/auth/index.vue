<script lang="ts" setup>
import { ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import UserAuth from './auth-user/UserAuth.vue'
const { t } = useI18n()

const tabArray = ref([
  { label: '按用户配置', name: 'userAuth' },
  { label: '按资源配置', name: 'resourceAuth' },
])

const activeName = ref('userAuth')

</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.auth_manager') }}</p>
  </div>
  <el-tabs v-model="activeName">
    <el-tab-pane v-for="item in tabArray" :key="item.name" :label="item.label" :name="item.name" />
  </el-tabs>
  <div class="container-content">
    <user-auth v-if="activeName === 'userAuth'" />
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
  height: calc(100vh - 181px);
  box-sizing: border-box;
  margin-top: 12px;
  border-radius: 4px;

  .left-container {
    width: 20%;
    .left-tabs {
      padding: 16px;
    }
  }
}
.container-header {
  height: 32px;
}

</style>
