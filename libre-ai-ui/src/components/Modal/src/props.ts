export const basicProps = {
  // 确认按钮文字
  subBtuText: {
    type: String,
    default: '确认'
  },
  // 取消按钮文字
  subCloseText: {
    type: String,
    default: '取消'
  },
  showIcon: {
    type: Boolean,
    default: false
  },
  width: {
    type: [String, Number],
    default: '50%'
  },
  title: {
    type: String,
    default: ''
  },
  closeOnClickModal: {
    type: Boolean,
    default: false
  },
  showCloseBtn: {
    type: Boolean,
    default: true
  },
  showSubBtn: {
    type: Boolean,
    default: true
  },
  destroyOnClose: {
    type: Boolean,
    default: false
  },
  modal: {
    type: Boolean,
    default: true
  },
  appendToBody: {
    type: Boolean,
    default: true
  },
  lockScroll: {
    type: Boolean,
    default: true
  },
  customClass: {
    type: String,
    default: ''
  },
  openDelay: {
    type: Number,
    default: 0
  },
  closeDelay: {
    type: Number,
    default: 0
  },
  top: {
    type: String,
    default: '15vh'
  },
  modelValue: {
    type: Boolean,
    default: false
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  showClose: {
    type: Boolean,
    default: true
  },
  beforeClose: {
    type: Function,
    default: null
  },
  draggable: {
    type: Boolean,
    default: false
  },
  center: {
    type: Boolean,
    default: false
  }
};
