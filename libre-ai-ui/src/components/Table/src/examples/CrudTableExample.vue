<template>
  <div class="crud-table-example">
    <div class="example-header mb-6">
      <h2 class="text-xl font-bold mb-2">CRUD 表格示例</h2>
      <p class="text-gray-600">基于配置的 CRUD 表格，支持搜索、分页、增删改查等完整功能</p>
    </div>

    <!-- CRUD 表格 -->
    <crud-table
      ref="crudTableRef"
      :options="crudOptions"
      theme="card"
      @add="handleAdd"
      @edit="handleEdit"
      @delete="handleDelete"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Delete, View, UserFilled } from '@element-plus/icons-vue'
import { CrudTable } from '../../index'
import type { CrudOptions } from '../crud-types'

// 表格引用
const crudTableRef = ref()

// 模拟用户数据
const mockUsers = [
  { id: 1, name: '张三', email: 'zhangsan@example.com', age: 25, status: 1, role: 'admin', createTime: '2024-01-01 10:00:00' },
  { id: 2, name: '李四', email: 'lisi@example.com', age: 30, status: 1, role: 'user', createTime: '2024-01-02 10:00:00' },
  { id: 3, name: '王五', email: 'wangwu@example.com', age: 28, status: 0, role: 'user', createTime: '2024-01-03 10:00:00' },
  { id: 4, name: '赵六', email: 'zhaoliu@example.com', age: 35, status: 1, role: 'editor', createTime: '2024-01-04 10:00:00' },
  { id: 5, name: '钱七', email: 'qianqi@example.com', age: 22, status: 0, role: 'user', createTime: '2024-01-05 10:00:00' }
]

// CRUD 配置
const crudOptions: CrudOptions = reactive({
  // 列配置
  columns: {
    id: {
      key: 'id',
      title: 'ID',
      width: 80,
      hideInSearch: true,
      hideInForm: true,
      table: {
        showOverflowTooltip: false
      }
    },
    name: {
      key: 'name',
      title: '姓名',
      width: 120,
      rules: [{ required: true, message: '请输入姓名' }],
      search: {
        component: 'input',
        placeholder: '请输入姓名'
      },
      add: {
        component: 'input',
        placeholder: '请输入姓名'
      },
      edit: {
        component: 'input',
        placeholder: '请输入姓名'
      }
    },
    email: {
      key: 'email',
      title: '邮箱',
      width: 200,
      rules: [
        { required: true, message: '请输入邮箱' },
        { type: 'email', message: '请输入正确的邮箱格式' }
      ],
      search: {
        component: 'input',
        placeholder: '请输入邮箱'
      },
      add: {
        component: 'input',
        placeholder: '请输入邮箱'
      },
      edit: {
        component: 'input',
        placeholder: '请输入邮箱'
      }
    },
    age: {
      key: 'age',
      title: '年龄',
      width: 80,
      hideInSearch: true,
      rules: [
        { required: true, message: '请输入年龄' },
        { type: 'number', min: 1, max: 150, message: '年龄必须在1-150之间' }
      ],
      add: {
        component: 'number',
        props: { min: 1, max: 150 }
      },
      edit: {
        component: 'number',
        props: { min: 1, max: 150 }
      }
    },
    status: {
      key: 'status',
      title: '状态',
      width: 100,
      dict: {
        data: [
          { label: '启用', value: 1, tagType: 'success' },
          { label: '禁用', value: 0, tagType: 'danger' }
        ]
      },
      search: {
        component: 'dict-select',
        placeholder: '请选择状态'
      },
      add: {
        component: 'dict-radio',
        defaultValue: 1
      },
      edit: {
        component: 'dict-radio'
      },
      table: {
        type: 'tag'
      }
    },
    role: {
      key: 'role',
      title: '角色',
      width: 120,
      dict: {
        data: [
          { label: '管理员', value: 'admin', tagType: 'danger' },
          { label: '编辑者', value: 'editor', tagType: 'warning' },
          { label: '普通用户', value: 'user', tagType: 'info' }
        ]
      },
      search: {
        component: 'dict-select',
        placeholder: '请选择角色'
      },
      add: {
        component: 'dict-select',
        defaultValue: 'user'
      },
      edit: {
        component: 'dict-select'
      },
      table: {
        type: 'tag'
      }
    },
    createTime: {
      key: 'createTime',
      title: '创建时间',
      width: 180,
      hideInForm: true,
      search: {
        component: 'daterange',
        placeholder: ['开始时间', '结束时间']
      },
      table: {
        type: 'date',
        dateFormat: 'YYYY-MM-DD HH:mm'
      }
    }
  },

  // 请求配置
  request: {
    // 分页查询
    pageRequest: async (params) => {
      console.log('查询参数:', params)

      // 模拟异步请求
      await new Promise(resolve => setTimeout(resolve, 500))

      let filteredData = [...mockUsers]

      // 模拟搜索过滤
      if (params.search) {
        const { name, email, status, role, createTime } = params.search

        if (name) {
          filteredData = filteredData.filter(item =>
            item.name.includes(name)
          )
        }

        if (email) {
          filteredData = filteredData.filter(item =>
            item.email.includes(email)
          )
        }

        if (status !== undefined && status !== '') {
          filteredData = filteredData.filter(item =>
            item.status === status
          )
        }

        if (role) {
          filteredData = filteredData.filter(item =>
            item.role === role
          )
        }

        if (createTime && createTime.length === 2) {
          const [start, end] = createTime
          filteredData = filteredData.filter(item =>
            item.createTime >= start && item.createTime <= end
          )
        }
      }

      // 模拟分页
      const total = filteredData.length
      const start = (params.page - 1) * params.size
      const end = start + params.size
      const data = filteredData.slice(start, end)

      return {
        data,
        total,
        current: params.page,
        size: params.size
      }
    },

    // 新增
    addRequest: async (row) => {
      console.log('新增用户:', row)
      await new Promise(resolve => setTimeout(resolve, 1000))

      const newUser = {
        ...row,
        id: Math.max(...mockUsers.map(u => u.id)) + 1,
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }

      mockUsers.push(newUser)
      return newUser
    },

    // 编辑
    editRequest: async (row) => {
      console.log('编辑用户:', row)
      await new Promise(resolve => setTimeout(resolve, 1000))

      const index = mockUsers.findIndex(u => u.id === row.id)
      if (index > -1) {
        mockUsers[index] = { ...mockUsers[index], ...row }
      }

      return row
    },

    // 删除
    delRequest: async (row) => {
      console.log('删除用户:', row)
      await new Promise(resolve => setTimeout(resolve, 500))

      const index = mockUsers.findIndex(u => u.id === row.id)
      if (index > -1) {
        mockUsers.splice(index, 1)
      }
    },

    // 批量删除
    batchDelRequest: async (rows) => {
      console.log('批量删除用户:', rows)
      await new Promise(resolve => setTimeout(resolve, 1000))

      const ids = rows.map(row => row.id)
      for (let i = mockUsers.length - 1; i >= 0; i--) {
        if (ids.includes(mockUsers[i].id)) {
          mockUsers.splice(i, 1)
        }
      }
    },

    // 导出
    exportRequest: async (params) => {
      console.log('导出数据:', params)
      await new Promise(resolve => setTimeout(resolve, 1000))
      ElMessage.success('导出成功（模拟）')
    }
  },

  // 工具栏配置
  toolbar: {
    show: true,
    add: {
      key: 'add',
      label: '新增用户',
      type: 'primary',
      onClick: () => console.log('工具栏新增')
    },
    buttons: [
      {
        key: 'import',
        label: '导入',
        type: 'default',
        onClick: () => ElMessage.info('导入功能（待开发）')
      },
      {
        key: 'template',
        label: '下载模板',
        type: 'default',
        onClick: () => ElMessage.info('下载模板（待开发）')
      }
    ],
    refresh: true,
    export: true,
    columns: true
  },

  // 行操作配置
  rowActions: [
    {
      key: 'view',
      label: '查看',
      type: 'primary',
      icon: View,
      onClick: (row, index) => {
        console.log('查看用户:', row)
        ElMessage.info(`查看用户: ${row.name}`)
      }
    },
    {
      key: 'edit',
      label: '编辑',
      type: 'warning',
      icon: Edit,
      onClick: (row, index) => {
        console.log('编辑用户:', row)
      }
    },
    {
      key: 'delete',
      label: '删除',
      type: 'danger',
      icon: Delete,
      confirm: {
        title: '确认删除',
        content: '确定要删除这个用户吗？'
      },
      onClick: (row, index) => {
        console.log('删除用户:', row)
      }
    }
  ],

  // 搜索配置
  search: {
    show: true,
    labelWidth: '80px',
    showReset: true,
    showCollapse: true,
    collapsed: false
  },

  // 表单配置
  form: {
    add: {
      labelWidth: '100px',
      columns: 2,
      dialog: {
        title: '新增用户',
        width: '600px'
      }
    },
    edit: {
      labelWidth: '100px',
      columns: 2,
      dialog: {
        title: '编辑用户',
        width: '600px'
      }
    },
    view: {
      labelWidth: '100px',
      columns: 2,
      dialog: {
        title: '查看用户',
        width: '600px'
      }
    }
  },

  // 分页配置
  pagination: {
    show: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    layout: 'total, sizes, prev, pager, next, jumper'
  },

  // 表格配置
  table: {
    stripe: true,
    border: false,
    size: 'default',
    rowKey: 'id',
    selection: {
      show: true
    }
  },

  // 权限配置
  permission: {
    add: undefined, // 无权限限制
    edit: undefined,
    view: undefined,
    remove: undefined
  }
})

// 事件处理
const handleAdd = (data: any) => {
  console.log('新增成功:', data)
  ElMessage.success('新增用户成功')
}

const handleEdit = (data: any) => {
  console.log('编辑成功:', data)
  ElMessage.success('编辑用户成功')
}

const handleDelete = (data: any) => {
  console.log('删除成功:', data)
  ElMessage.success('删除用户成功')
}
</script>

<style lang="scss" scoped>
.crud-table-example {
  padding: 20px;

  .example-header {
    border-bottom: 1px solid var(--el-border-color-light);
    padding-bottom: 16px;
  }
}
</style>
