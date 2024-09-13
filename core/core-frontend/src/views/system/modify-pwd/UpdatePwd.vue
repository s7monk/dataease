<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { cloneDeep } from 'lodash-es'
import { logoutApi } from '@/api/login'
const userStore = useUserStoreWithOut()
import { ElMessage } from 'element-plus-secondary'
import { logoutHandler } from '@/utils/logout'
import { CustomPassword } from '@/components/custom-password'
import {useUserStoreWithOut} from "@/store/modules/user";
import { userResetPasswordApi } from "@/api/user";

const { t } = useI18n()

const defaultForm = {
  pwd: '',
  newPwd: '',
  confirm: ''
}
const pwdForm = reactive(cloneDeep(defaultForm))

const validatePwd = (_: any, value: any, callback: any) => {
  if (value === pwdForm.pwd) {
    callback(new Error('新旧密码不能相同'))
  }
  callback();
  /*const pattern =
    /^.*(?=.{6,20})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()_+\-\={}|":<>?`[\];',.\/])[a-zA-Z0-9~!@#$%^&*()_+\-\={}|":<>?`[\];',.\/]*$/
  const regep = new RegExp(pattern)
  if (!regep.test(value)) {
    const msg = t('user.pwd_pattern_error')
    callback(new Error(msg))
  } else {
    callback()
  }*/
}

const validateConfirmPwd = (_: any, value: any, callback: any) => {
  if (value !== pwdForm.newPwd) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rule = {
  pwd: [
    {
      required: true,
      message: t('common.require'),
      trigger: 'blur'
    },
    {
      min: 1,
      max: 30,
      message: t('commons.input_limit', [1, 30]),
      trigger: 'blur'
    }
  ],
  newPwd: [
    {
      required: true,
      message: t('common.require'),
      trigger: 'blur'
    },
    { validator: validatePwd, trigger: 'blur' }
  ],
  confirm: [
    {
      required: true,
      message: t('common.require'),
      trigger: 'blur'
    },
    {
      min: 1,
      max: 30,
      message: t('commons.input_limit', [1, 30]),
      trigger: 'blur'
    },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}
const updatePwdForm = ref()

const save = () => {
  updatePwdForm.value.validate(val => {
    if (val) {
      const newPwd = pwdForm.newPwd
      const data = { id: userStore.getUid, password: newPwd };
      userResetPasswordApi(data).then(res => {
        if (res.data.code === 200) {
          ElMessage.success('修改成功，请重新登录')
          logoutApi()
          logoutHandler()
        } else {
          throw new Error('Failed to update password');
        }
      }).catch(error => {
        console.error('修改密码失败:', error);
      });
    }
  })
}
</script>

<template>
  <el-form
    ref="updatePwdForm"
    require-asterisk-position="right"
    :model="pwdForm"
    :rules="rule"
    class="mt16"
    label-width="80px"
    label-position="top"
  >
    <el-form-item label="原始密码" prop="pwd">
      <CustomPassword
        v-model="pwdForm.pwd"
        show-password
        type="password"
        placeholder="请输入原始密码"
      />
    </el-form-item>
    <el-form-item label="新密码" prop="newPwd">
      <CustomPassword
        v-model="pwdForm.newPwd"
        show-password
        type="password"
        placeholder="请输入新密码"
      />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirm">
      <CustomPassword
        v-model="pwdForm.confirm"
        show-password
        type="password"
        placeholder="请输入确认密码"
      />
    </el-form-item>
    <el-button @click="save" type="primary">
      {{ t('common.save') }}
    </el-button>
  </el-form>
</template>

<style lang="less" scoped>
.mt16 {
  margin-top: 16px;
  .ed-form-item {
    margin-bottom: 16px;
    :deep(label) {
      line-height: 22px !important;
    }
  }
}
</style>
