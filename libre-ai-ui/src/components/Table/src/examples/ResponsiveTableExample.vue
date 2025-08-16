<template>
  <div class="responsive-table-examples">
    <h2>响应式表格示例</h2>
    
    <!-- 示例1: 基础响应式表格 -->
    <div class="example-section">
      <h3>1. 基础响应式表格</h3>
      <p class="description">
        在不同屏幕尺寸下自动调整列显示和宽度。
        <br>
        <small>
          💡 提示：缩放浏览器窗口查看响应式效果
        </small>
      </p>
      
      <AdvancedTable
        :columns="basicColumns"
        :data="sampleData"
        :search-config="searchConfig"
        :pagination-config="paginationConfig"
        :action-column="actionColumn"
        stripe
        show-index
      />
    </div>
    
    <!-- 示例2: 自定义响应式配置 -->
    <div class="example-section">
      <h3>2. 自定义响应式配置</h3>
      <p class="description">
        为不同列配置在移动端和平板端的显示规则。
      </p>
      
      <AdvancedTable
        :columns="customResponsiveColumns"
        :data="detailedData"
        :search-config="detailedSearchConfig"
        :pagination-config="paginationConfig"
        stripe
        show-index
      />
    </div>
    
    <!-- 示例3: 固定列响应式 -->
    <div class="example-section">
      <h3>3. 固定列响应式表格</h3>
      <p class="description">
        在移动端保持重要列固定，其他列可横向滚动。
      </p>
      
      <AdvancedTable
        :columns="fixedColumns"
        :data="wideData"
        :pagination-config="paginationConfig"
        border
        height="400px"
      />
    </div>
    
    <!-- 控制面板 -->
    <div class="control-panel">
      <h3>响应式控制面板</h3>
      <div class="controls">
        <div class="control-item">
          <span>当前屏幕宽度: </span>
          <el-tag type="primary">{{ screenWidth }}px</el-tag>
        </div>
        <div class="control-item">
          <span>设备类型: </span>
          <el-tag :type="deviceTypeColor">{{ deviceType }}</el-tag>
        </div>
        <div class="control-item">
          <el-button @click="simulateMobile">模拟移动端</el-button>
          <el-button @click="simulateTablet">模拟平板端</el-button>
          <el-button @click="simulateDesktop">模拟桌面端</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElTag, ElButton } from 'element-plus'
import { AdvancedTable } from '../index'

// 响应式状态
const screenWidth = ref(window.innerWidth)
const containerEl = ref<HTMLElement>()

// 设备类型判断
const deviceType = computed(() => {
  if (screenWidth.value <= 480) return '超小屏'
  if (screenWidth.value <= 768) return '移动端'
  if (screenWidth.value <= 1024) return '平板端'
  return '桌面端'
})

const deviceTypeColor = computed(() => {
  if (screenWidth.value <= 480) return 'danger'
  if (screenWidth.value <= 768) return 'warning'
  if (screenWidth.value <= 1024) return 'info'
  return 'success'
})

// 基础示例数据
const sampleData = [
  {
    id: 1,
    name: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    department: '技术部',
    position: '前端工程师',
    status: 'active',
    joinDate: '2023-01-15',
    salary: 15000
  },
  {
    id: 2,
    name: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    department: '产品部',
    position: '产品经理',
    status: 'inactive',
    joinDate: '2023-03-20',
    salary: 18000
  },
  {
    id: 3,
    name: '王五',
    email: 'wangwu@example.com',
    phone: '13800138003',
    department: '设计部',
    position: 'UI设计师',
    status: 'active',
    joinDate: '2023-05-10',
    salary: 12000
  }
]

// 详细示例数据
const detailedData = [
  {
    id: 1,
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    name: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    department: '技术部',
    position: '前端工程师',
    level: 'P6',
    experience: '3年',
    skills: ['Vue', 'React', 'TypeScript'],
    status: 'active',
    joinDate: '2023-01-15',
    lastLogin: '2024-01-20 10:30:00',
    performance: 85,
    salary: 15000,
    bonus: 2000,
    address: '北京市朝阳区xxx街道',
    remark: '技术能力强，工作积极'
  },
  {
    id: 2,
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    name: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    department: '产品部',
    position: '产品经理',
    level: 'P7',
    experience: '5年',
    skills: ['需求分析', '原型设计', '项目管理'],
    status: 'inactive',
    joinDate: '2023-03-20',
    lastLogin: '2024-01-19 16:45:00',
    performance: 92,
    salary: 18000,
    bonus: 3000,
    address: '上海市浦东新区xxx街道',
    remark: '沟通能力强，执行力优秀'
  }
]

// 宽表格数据
const wideData = [
  {
    id: 1,
    name: '项目A',
    code: 'PROJ-001',
    type: '产品开发',
    priority: 'high',
    status: 'progress',
    owner: '张三',
    team: '技术部',
    startDate: '2024-01-01',
    endDate: '2024-06-30',
    progress: 65,
    budget: 500000,
    spent: 320000,
    remaining: 180000,
    milestone1: '需求分析',
    milestone2: '技术选型',
    milestone3: '开发测试',
    milestone4: '上线部署',
    risk: 'medium',
    quality: 'good',
    client: '客户A',
    description: '这是一个重要的产品开发项目'
  }
]

// 基础响应式列配置
const basicColumns = [
  {
    key: 'name',
    title: '姓名',
    width: 120,
    required: true, // 必要列，所有设备都显示
    fixed: 'left'
  },
  {
    key: 'email',
    title: '邮箱',
    width: 200,
    hideOnMobile: true, // 移动端隐藏
    showOverflowTooltip: true
  },
  {
    key: 'phone',
    title: '电话',
    width: 140,
    hideOnMobile: true
  },
  {
    key: 'department',
    title: '部门',
    width: 120,
    hideOnTablet: true // 平板端隐藏
  },
  {
    key: 'position',
    title: '职位',
    width: 140
  },
  {
    key: 'status',
    title: '状态',
    width: 100,
    type: 'tag',
    tagOptions: {
      'active': { label: '在职', type: 'success' },
      'inactive': { label: '离职', type: 'info' }
    }
  },
  {
    key: 'joinDate',
    title: '入职日期',
    width: 120,
    type: 'date',
    hideOnMobile: true
  },
  {
    key: 'salary',
    title: '薪资',
    width: 100,
    type: 'number',
    hideOnMobile: true,
    hideOnTablet: true
  }
]

// 自定义响应式列配置
const customResponsiveColumns = [
  {
    key: 'avatar',
    title: '头像',
    width: 80,
    type: 'image',
    imageWidth: '40px',
    imageHeight: '40px',
    hideOnMobile: true
  },
  {
    key: 'name',
    title: '姓名',
    width: 120,
    required: true,
    fixed: 'left'
  },
  {
    key: 'email',
    title: '邮箱',
    width: 200,
    showOverflowTooltip: true,
    hideOnMobile: true
  },
  {
    key: 'department',
    title: '部门',
    width: 120
  },
  {
    key: 'position',
    title: '职位',
    width: 140
  },
  {
    key: 'level',
    title: '级别',
    width: 80,
    hideOnMobile: true
  },
  {
    key: 'experience',
    title: '经验',
    width: 100,
    hideOnTablet: true
  },
  {
    key: 'performance',
    title: '绩效',
    width: 120,
    type: 'progress',
    hideOnMobile: true
  },
  {
    key: 'status',
    title: '状态',
    width: 100,
    type: 'tag',
    tagOptions: {
      'active': { label: '在职', type: 'success' },
      'inactive': { label: '离职', type: 'info' }
    }
  },
  {
    key: 'lastLogin',
    title: '最后登录',
    width: 160,
    type: 'date',
    hideOnMobile: true,
    hideOnTablet: true
  }
]

// 固定列配置
const fixedColumns = [
  {
    key: 'name',
    title: '项目名称',
    width: 120,
    fixed: 'left',
    required: true
  },
  {
    key: 'code',
    title: '项目编号',
    width: 120,
    fixed: 'left'
  },
  {
    key: 'type',
    title: '类型',
    width: 100
  },
  {
    key: 'priority',
    title: '优先级',
    width: 100,
    type: 'tag',
    tagOptions: {
      'high': { label: '高', type: 'danger' },
      'medium': { label: '中', type: 'warning' },
      'low': { label: '低', type: 'info' }
    }
  },
  {
    key: 'status',
    title: '状态',
    width: 100,
    type: 'tag',
    tagOptions: {
      'progress': { label: '进行中', type: 'primary' },
      'completed': { label: '已完成', type: 'success' },
      'paused': { label: '已暂停', type: 'warning' }
    }
  },
  {
    key: 'owner',
    title: '负责人',
    width: 100
  },
  {
    key: 'team',
    title: '团队',
    width: 100,
    hideOnMobile: true
  },
  {
    key: 'progress',
    title: '进度',
    width: 120,
    type: 'progress'
  },
  {
    key: 'budget',
    title: '预算',
    width: 120,
    type: 'number',
    hideOnMobile: true
  },
  {
    key: 'spent',
    title: '已花费',
    width: 120,
    type: 'number',
    hideOnTablet: true
  },
  {
    key: 'remaining',
    title: '剩余',
    width: 120,
    type: 'number',
    hideOnTablet: true
  },
  {
    key: 'startDate',
    title: '开始日期',
    width: 120,
    type: 'date',
    hideOnMobile: true
  },
  {
    key: 'endDate',
    title: '结束日期',
    width: 120,
    type: 'date',
    hideOnMobile: true
  }
]

// 搜索配置
const searchConfig = {
  fields: [
    {
      key: 'name',
      label: '姓名',
      type: 'input',
      placeholder: '请输入姓名'
    },
    {
      key: 'department',
      label: '部门',
      type: 'select',
      options: [
        { label: '技术部', value: '技术部' },
        { label: '产品部', value: '产品部' },
        { label: '设计部', value: '设计部' }
      ]
    },
    {
      key: 'status',
      label: '状态',
      type: 'select',
      options: [
        { label: '在职', value: 'active' },
        { label: '离职', value: 'inactive' }
      ],
      collapsed: true // 默认收起
    }
  ],
  showCollapse: true
}

const detailedSearchConfig = {
  fields: [
    {
      key: 'name',
      label: '姓名',
      type: 'input'
    },
    {
      key: 'department',
      label: '部门',
      type: 'select',
      options: [
        { label: '技术部', value: '技术部' },
        { label: '产品部', value: '产品部' }
      ]
    },
    {
      key: 'level',
      label: '级别',
      type: 'select',
      options: [
        { label: 'P6', value: 'P6' },
        { label: 'P7', value: 'P7' }
      ],
      collapsed: true
    },
    {
      key: 'joinDate',
      label: '入职日期',
      type: 'daterange',
      collapsed: true
    }
  ],
  showCollapse: true,
  defaultExpanded: false
}

// 分页配置
const paginationConfig = {
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true
}

// 操作列配置
const actionColumn = {
  width: 160,
  actions: [
    {
      icon: 'edit',
      label: '编辑',
      type: 'primary',
      onClick: (row: any) => {
        console.log('编辑', row)
      }
    },
    {
      icon: 'view',
      label: '查看',
      onClick: (row: any) => {
        console.log('查看', row)
      }
    },
    {
      icon: 'delete',
      label: '删除',
      type: 'danger',
      onClick: (row: any) => {
        console.log('删除', row)
      }
    }
  ]
}

// 窗口大小变化处理
const handleResize = () => {
  screenWidth.value = window.innerWidth
}

// 模拟不同设备
const simulateMobile = () => {
  if (containerEl.value) {
    containerEl.value.style.maxWidth = '375px'
    containerEl.value.style.margin = '0 auto'
  }
}

const simulateTablet = () => {
  if (containerEl.value) {
    containerEl.value.style.maxWidth = '768px'
    containerEl.value.style.margin = '0 auto'
  }
}

const simulateDesktop = () => {
  if (containerEl.value) {
    containerEl.value.style.maxWidth = 'none'
    containerEl.value.style.margin = '0'
  }
}

// 生命周期
onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.responsive-table-examples {
  padding: 20px;
  
  .example-section {
    margin-bottom: 40px;
    
    h3 {
      margin: 20px 0 10px 0;
      color: var(--el-text-color-primary);
      font-size: 16px;
      font-weight: 600;
    }
    
    .description {
      margin-bottom: 20px;
      color: var(--el-text-color-regular);
      font-size: 14px;
      line-height: 1.6;
      
      small {
        color: var(--el-text-color-placeholder);
        font-size: 12px;
      }
    }
  }
  
  .control-panel {
    margin-top: 40px;
    padding: 20px;
    background: var(--el-bg-color-page);
    border-radius: 8px;
    
    h3 {
      margin: 0 0 20px 0;
      color: var(--el-text-color-primary);
    }
    
    .controls {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      align-items: center;
      
      .control-item {
        display: flex;
        align-items: center;
        gap: 8px;
        
        span {
          color: var(--el-text-color-regular);
          font-size: 14px;
        }
      }
    }
  }
}

// 模拟容器样式
.responsive-container {
  transition: all 0.3s ease;
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  padding: 10px;
}

@media (max-width: 768px) {
  .responsive-table-examples {
    padding: 10px;
    
    .control-panel {
      .controls {
        flex-direction: column;
        align-items: stretch;
        
        .control-item {
          justify-content: space-between;
        }
      }
    }
  }
}
</style>