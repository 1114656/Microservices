<script lang="ts" setup>
import { PropType } from 'vue'
import { Editor, Toolbar } from '@wangeditor-next/editor-for-vue'
import { i18nChangeLanguage, IDomEditor, IEditorConfig } from '@wangeditor-next/editor'
import { propTypes } from '@/utils/propTypes'
import { isNumber } from '@/utils/is'
import { ElMessage } from 'element-plus'
import { useLocaleStore } from '@/store/modules/locale'
import merge from 'lodash-es/merge'

defineOptions({ name: 'Editor' })

const localeStore = useLocaleStore()
const currentLocale = computed(() => localeStore.getCurrentLocale)
i18nChangeLanguage(unref(currentLocale).lang)

const props = defineProps({
  editorId: propTypes.string.def('wangEditor-1'),
  height: propTypes.oneOfType([Number, String]).def('500px'),
  editorConfig: {
    type: Object as PropType<Partial<IEditorConfig>>,
    default: () => undefined
  },
  readonly: propTypes.bool.def(false),
  modelValue: propTypes.string.def('')
})

const emit = defineEmits(['change', 'update:modelValue'])
const editorRef = shallowRef<IDomEditor>()
const valueHtml = ref('')

watch(
  () => props.modelValue,
  (val: string) => {
    const nextVal = val || ''
    if (nextVal === unref(valueHtml)) return
    valueHtml.value = nextVal
  },
  { immediate: true }
)

watch(
  () => valueHtml.value,
  (val: string) => {
    emit('update:modelValue', val)
  }
)

watch(
  () => props.readonly,
  async (val) => {
    if (!editorRef.value) await nextTick()
    if (val) editorRef.value?.disable()
    else editorRef.value?.enable()
  }
)

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor
}

const editorConfig = computed((): IEditorConfig => {
  return merge(
    {
      placeholder: '请输入内容...',
      readOnly: props.readonly,
      customAlert: (message: string, type: string) => {
        if (type === 'success') ElMessage.success(message)
        else if (type === 'warning') ElMessage.warning(message)
        else if (type === 'error') ElMessage.error(message)
        else ElMessage.info(message)
      },
      autoFocus: false,
      scroll: true,
      EXTEND_CONF: {
        mentionConfig: {
          showModal: () => {},
          hideModal: () => {}
        }
      },
      uploadImgShowBase64: true
    },
    props.editorConfig || {}
  )
})

const editorStyle = computed(() => ({
  height: isNumber(props.height) ? `${props.height}px` : props.height
}))

const handleChange = (editor: IDomEditor) => {
  emit('change', editor)
}

onBeforeUnmount(() => {
  unref(editorRef.value)?.destroy()
})

const getEditorRef = async (): Promise<IDomEditor> => {
  await nextTick()
  return unref(editorRef.value) as IDomEditor
}

defineExpose({ getEditorRef })
</script>

<template>
  <div class="border-1 border-solid border-[var(--tags-view-border-color)] z-10">
    <Toolbar
      :editor="editorRef"
      :editorId="editorId"
      class="border-0 b-b-1 border-solid border-[var(--tags-view-border-color)]"
    />
    <Editor
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      :editorId="editorId"
      :style="editorStyle"
      @on-change="handleChange"
      @on-created="handleCreated"
    />
  </div>
</template>

<style src="@wangeditor-next/editor/dist/css/style.css"></style>
