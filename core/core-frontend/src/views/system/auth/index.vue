<script lang="ts" setup>
import { ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
const { t } = useI18n()
</script>
<template>
  <div class="container-header">
    <p class="router-title">{{ t('system.auth_manager') }}</p>
  </div>
  <div class="container-content">

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
  margin-top: 16px;
}
.container-header {
  height: 32px;
}

</style>
