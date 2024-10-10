<script lang="ts" setup>
import { Icon } from '@/components/icon-custom'
import { propTypes } from '@/utils/propTypes'
import type { Placement } from 'element-plus-secondary'
import { ref, PropType, watch } from 'vue'
import ShareHandler from '@/views/share/share/ShareHandler.vue'
export interface Menu {
  svgName?: string
  label?: string
  command: string
  divided?: boolean
  disabled?: boolean
  hidden?: boolean
}

const props = defineProps({
  menuList: {
    type: Array as PropType<Menu[]>
  },
  placement: {
    type: String as () => Placement,
    default: 'bottom-end'
  },
  iconName: propTypes.string.def('icon_more_outlined'),
  inTable: propTypes.bool.def(false),
  resourceType: propTypes.string.def('dashboard'),
  node: {
    type: Object,
    default() {
      return {}
    }
  },
  anyManage: propTypes.bool.def(false)
})

const shareComponent = ref(null)

const menus = ref<Menu[]>([])
const updateMenus = () => {
  menus.value = JSON.parse(JSON.stringify(props.menuList)).map((item: Menu) => {
    if (((!props.node || props.node.isManage === false) &&
      ['copy', 'move', 'rename', 'delete'].includes(item.command))
      || ((!props.node || props.node.isShare === false) && item.command === 'share')) {
      item.hidden = true
    }
    return item
  })
}

watch(() => props.node, () => {
  updateMenus()
}, { deep: true, immediate: true })

const handleCommand = (command: string | number | object) => {
  if (command === 'share') {
    // shareComponent.value.invokeMethod({ methodName: 'execute' })
    shareComponent.value.execute()
    return
  }
  emit('handleCommand', command)
}
const callBack = param => {
  if (props.node.leaf && props.node?.weight >= 7) {
    menus.value[0]['divided'] = true
    menus.value.splice(0, 0, param)
  }
}
const emit = defineEmits(['handleCommand'])
</script>

<template>
  <el-dropdown
    popper-class="menu-more-dv_popper"
    :placement="placement"
    trigger="click"
    @command="handleCommand"
  >
    <el-icon class="hover-icon" :class="inTable && 'hover-icon-in-table'" @click.stop>
      <Icon :name="iconName"></Icon>
    </el-icon>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          :divided="ele.divided"
          :command="ele.command"
          v-for="ele in menus"
          :key="ele.label"
          :disabled="ele.disabled"
          :class="{ 'de-hidden-drop-item': ele.hidden }"
        >
          <el-icon class="handle-icon" v-if="ele.svgName">
            <Icon :name="ele.svgName"></Icon>
          </el-icon>
          {{ ele.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <ShareHandler
    ref="shareComponent"
    :resource-id="props.node.id"
    :resource-type="props.resourceType"
    :weight="node.weight"
    @loaded="callBack"
    :isShare="node.isShare"
  />
</template>

<style lang="less">
.de-hidden-drop-item {
  display: none;
}
.menu-more-dv_popper {
  width: 120px;
  margin-top: -2px !important;
}

.handle-icon {
  font-size: 16px;
  color: #646a73;
}
</style>
