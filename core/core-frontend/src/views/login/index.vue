<script lang="ts" setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { FormRules, FormInstance } from 'element-plus-secondary'
import { loginApi, queryDekey, loginCategoryApi } from '@/api/login'
import { useCache } from '@/hooks/web/useCache'
import { useAppStoreWithOut } from '@/store/modules/app'
import { CustomPassword } from '@/components/custom-password'
import { useUserStoreWithOut } from '@/store/modules/user'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'
import router from '@/router'
import { ElMessage } from 'element-plus-secondary'
import { logoutHandler } from '@/utils/logout'
import DeImage from '@/assets/login-desc-de.png'
import elementResizeDetectorMaker from 'element-resize-detector'
import { checkPlatform, cleanPlatformFlag } from '@/utils/utils'
import xss from 'xss'
const { wsCache } = useCache()
const appStore = useAppStoreWithOut()
const userStore = useUserStoreWithOut()
const appearanceStore = useAppearanceStoreWithOut()
const { t } = useI18n()
const contentShow = ref(true)
const loading = ref(false)
const axiosFinished = ref(true)
const showFoot = ref(true)

const loginLogoUrl = ref(null)
const msg = ref(null)
const loginImageUrl = ref(null)
const slogan = ref(null)
const footContent = ref(null)
const loginErrorMsg = ref('')
const xpackLoginHandler = ref()
const showDempTips = ref(false)
const demoTips = computed(() => {
  if (!showDempTips.value) {
    return ''
  }
})
const state = reactive({
  loginForm: {
    username: '',
    password: ''
  },
  footContent: ''
})

const rules = reactive<FormRules>({
  username: [{ required: true, message: t('common.required'), trigger: 'blur' }],
  password: [{ required: true, message: t('common.required'), trigger: 'blur' }]
})

const activeName = ref('simple')

const getCurLocation = () => {
  let queryRedirectPath = '/workbranch/index'
  if (router.currentRoute.value.query.redirect) {
    queryRedirectPath = router.currentRoute.value.query.redirect as string
  }
  return queryRedirectPath
}

const formRef = ref<FormInstance | undefined>()
const duringLogin = ref(false)
const handleLogin = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const name = state.loginForm.username.trim()
      const pwd = state.loginForm.password
      if (!wsCache.get(appStore.getDekey)) {
        const res = await queryDekey()
        wsCache.set(appStore.getDekey, res.data)
      }
      const param = { username: name, password: pwd }
      duringLogin.value = true
      cleanPlatformFlag()
      loginApi(param)
        .then(res => {
          const { tokenValue, tokenTimeout } = res.data.data.saTokenInfo
          const menuList = res.data.data.sysMenuList
          const allPerms = menuList.map(menu => menu.perms).filter(perm => perm);
          userStore.setPerms(allPerms);
          userStore.setToken(tokenValue)
          userStore.setExp(tokenTimeout)
          const { id, username, nickname, admin } = res.data.data.user
          userStore.setUid(id)
          userStore.setName(username)
          userStore.setNickname(nickname)
          userStore.setIsAdmin(admin)
          const queryRedirectPath = getCurLocation()
          router.push({ path: queryRedirectPath })
        })
        .catch(() => {
          duringLogin.value = false;
        })
    }
  })
}

const loadingText = ref('登录中...')
const loginContainer = ref()
const loginContainerWidth = ref(0)
const showLoginImage = computed<boolean>(() => {
  return !(loginContainerWidth.value < 889)
})

const preheat = ref(true)
const showLoginErrorMsg = () => {
  if (!loginErrorMsg.value) {
    return
  }
  if (loginErrorMsg.value.startsWith('token is empty')) {
    ElMessage.error('token为空！')
    return
  }
  if (loginErrorMsg.value.startsWith('token is Expired')) {
    ElMessage.error('token已过期，请重新登录！')
    return
  }
  if (loginErrorMsg.value.startsWith('token is destroyed')) {
    ElMessage.error('token已销毁，请重新登录！')
    return
  }
  if (loginErrorMsg.value.startsWith('user_disable')) {
    ElMessage.error('用户已被禁用，无法登录！')
    return
  }
  if (loginErrorMsg.value.startsWith('permission has been changed')) {
    ElMessage.error('默认组织已发生变更，请重新登录！')
    return
  }
  ElMessage.error(loginErrorMsg.value)
}

onMounted(async () => {
  if (!checkPlatform()) {
    const res = await loginCategoryApi()
    const adminLogin = router.currentRoute?.value?.name === 'admin-login'
    if (adminLogin && (!res.data || res.data === 1)) {
      router.push('/401')
      return
    }
    if (res.data && !adminLogin) {
      if (res.data === 1) {
        activeName.value = 'ldap'
        preheat.value = false
      } else {
        loadingText.value = '加载中...'
        document.getElementsByClassName('ed-loading-text')?.length &&
          (document.getElementsByClassName('ed-loading-text')[0]['innerText'] = loadingText.value)
      }
      nextTick(() => {
        const param = { methodName: 'ssoLogin', args: res.data }
        const timer = setInterval(() => {
          if (xpackLoginHandler?.value.invokeMethod) {
            xpackLoginHandler?.value.invokeMethod(param)
            clearInterval(timer)
          }
        }, 1000)
      })
    } else {
      preheat.value = false
    }
  }
  if (localStorage.getItem('DE-GATEWAY-FLAG')) {
    const msg = localStorage.getItem('DE-GATEWAY-FLAG')
    loginErrorMsg.value = decodeURIComponent(msg)
    showLoginErrorMsg()
    localStorage.removeItem('DE-GATEWAY-FLAG')
    logoutHandler(true)
  }
  if (!wsCache.get(appStore.getDekey)) {
    queryDekey().then(res => {
      wsCache.set(appStore.getDekey, res.data)
    })
  }
  const erd = elementResizeDetectorMaker()
  erd.listenTo(loginContainer.value, () => {
    nextTick(() => {
      loginContainerWidth.value = loginContainer.value?.offsetWidth
    })
  })
})
</script>

<template>
  <div
    v-if="preheat"
    ref="loginContainer"
    class="preheat-container"
    v-loading="true"
    :element-loading-text="loadingText"
    element-loading-background="#F5F6F7"
  />
  <div v-show="contentShow" class="login-background" v-loading="duringLogin">
    <div class="login-container" ref="loginContainer">
      <div class="login-image-content" v-loading="!axiosFinished" v-if="showLoginImage">
        <el-image
          v-if="axiosFinished"
          class="login-image"
          fit="cover"
          :src="loginImageUrl || DeImage"
        />
      </div>
      <div class="login-form-content" v-loading="loading">
        <div class="login-form-center">
          <el-form
            ref="formRef"
            :model="state.loginForm"
            :rules="rules"
            size="default"
            :disabled="preheat"
          >
            <div class="login-logo">
              <span class = "logo2">巴别时代</span>
<!--              <Icon
                v-if="!loginLogoUrl && axiosFinished"
                className="login-logo-icon"
                name="DataEase"
              ></Icon>-->
              <img v-if="loginLogoUrl && axiosFinished" :src="loginLogoUrl" alt="" />
            </div>
            <div class="login-welcome">
              {{ slogan || '欢迎使用巴别数据平台' }}
            </div>
            <div class="login-form">
              <div class="default-login-tabs" v-if="activeName === 'simple'">
                <div class="login-form-title">
                  <span>账号登录</span>
                </div>
                <el-form-item class="login-form-item" prop="username">
                  <el-input
                    v-model="state.loginForm.username"
                    :placeholder="t('common.account')"
                    autofocus
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <CustomPassword
                    v-model="state.loginForm.password"
                    :placeholder="t('common.pwd')"
                    show-password
                    maxlength="30"
                    show-word-limit
                    autocomplete="new-password"
                    @keypress.enter="handleLogin"
                  />
                </el-form-item>
                <div class="login-btn">
                  <el-button
                    type="primary"
                    class="submit"
                    size="default"
                    :disabled="duringLogin"
                    @click="handleLogin"
                  >
                    {{ t('login.btn') }}
                  </el-button>
                  <div v-if="showDempTips" class="demo-tips">
                    <span>{{ demoTips }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="login-msg">
              {{ msg }}
            </div>
          </el-form>
        </div>
        <div v-if="showFoot" class="dynamic-login-foot" v-html="footContent" />
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.preheat-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
  position: absolute;
  z-index: 100;
}
.login-background {
  background-color: #f5f7fa;
  height: 100vh;
  width: 100vw;
}

.login-container {
  width: 100%;
  height: 100%;
  background-color: var(--ContentBG, #ffffff);
  display: flex;
  .login-image-content {
    overflow: hidden;
    height: 100%;
    width: 40%;
    min-width: 400px;
  }

  .login-form-content {
    position: relative;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .login-form-center {
      width: 480px;
    }
  }
  .login-logo {
    text-align: center;
    img {
      width: auto;
      max-height: 52px;
      @media only screen and (max-width: 1280px) {
        width: auto;
        max-height: 52px;
      }
    }
  }

  .login-title {
    margin-top: 50px;
    font-size: 32px;
    letter-spacing: 0;
    text-align: center;
    color: #999999;

    @media only screen and (max-width: 1280px) {
      margin-top: 20px;
    }
  }

  .login-border {
    height: 2px;
    margin: 20px auto 20px;
    position: relative;
    width: 80px;
    background: var(--ed-color-primary);
    @media only screen and (max-width: 1280px) {
      margin: 20px auto 20px;
    }
  }

  .login-welcome {
    text-align: center;
    margin-top: 8px;
    color: #646a73;
    font-family: '阿里巴巴普惠体 3.0 55 Regular L3';
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 20px;
  }

  .demo-tips {
    position: absolute;
    font-size: 18px;
    color: #f56c6c;
    letter-spacing: 0;
    line-height: 18px;
    text-align: center;
    top: 120px;
    @media only screen and (max-width: 1280px) {
      margin-top: 20px;
    }
  }

  .login-form {
    margin-top: 40px;
    padding: 40px;
    padding-top: 20px;
    box-shadow: 0px 6px 24px rgba(31, 35, 41, 0.08);
    border: 1px solid #dee0e3;
    border-radius: 4px;

    .login-form-item {
      margin-top: 24px;
    }

    .ed-form-item--default {
      margin-bottom: 24px;
    }
    .login-form-title {
      margin-top: 20px;
      color: #1f2329;
      font-family: PingFang SC;
      font-size: 20px;
      font-weight: 500;
      line-height: 28px;
      text-align: left;
    }
  }

  :deep(.ed-divider__text) {
    color: #8f959e;
    font-family: '阿里巴巴普惠体 3.0 55 Regular L3';
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: 20px;
    padding: 0 8px;
  }

  .login-btn {
    position: relative;
    margin-bottom: 120px;
    .submit {
      width: 100%;
      height: 40px;
      line-height: 40px;
    }
  }

  .login-msg {
    margin-top: 10px;
    padding: 0 40px;
    color: #f56c6c;
    text-align: center;
  }

  .login-image {
    //object-fit: cover;
    //background: url(../../assets/login-desc-de.png);
    background-size: 100% 100%;
    width: 100%;
    height: 100%;
  }
  .login-image-de {
    background-size: cover;
    width: 100%;
    height: 520px;
    @media only screen and (max-width: 1280px) {
      height: 380px;
    }
  }
}
.dynamic-login-foot {
  visibility: visible;
  width: 100%;
  position: absolute;
  z-index: 302;
  bottom: 0;
  left: 0;
  height: auto;
  padding-top: 1px;
  zoom: 1;
  margin: 0;
}

.login-logo-icon {
  width: auto;
  height: 52px;
}

.logo2 {
  font-size: 24px; /* 设置字体大小 */
  font-weight: bold; /* 字体加粗 */
  color: #ffffff; /* 字体颜色设置为白色 */
  cursor: pointer; /* 鼠标悬停时显示指针形状 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); /* 增强文字阴影效果，使文字更突出 */
  padding: 10px 60px; /* 调整内边距 */
  border-radius: 5px; /* 边角圆滑处理 */
  display: inline-block; /* 使元素像块级元素一样显示，但仍然在行内 */
 /* font-style: italic; !* 斜体 *!*/
  background: linear-gradient(135deg, #0056b3 0%, #0096c7 100%); /* 添加蓝色渐变背景 */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2); /* 添加盒子阴影，增加立体感 */
  transition: all 0.3s ease; /* 添加过渡动画，使鼠标悬停和其他交互更平滑 */
}

.logo2:hover {
  transform: scale(1.05); /* 鼠标悬停时轻微放大 */
  background: linear-gradient(135deg, #0096c7 0%, #0056b3 100%); /* 鼠标悬停时改变渐变方向 */
}
</style>
