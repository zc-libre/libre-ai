<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus';
import {
  Plus,
  Edit,
  Delete,
  View,
  Search,
  Grid,
  List,
  Filter,
  Refresh,
  Operation,
  DataAnalysis,
  Connection
} from '@element-plus/icons-vue';
import { del, list as getList } from '@/api/aigc/app';
import AppEditModal from './AppEditModal.vue';

const router = useRouter();
const loading = ref(false);
const apps = ref<any[]>([]);
const editModalRef = ref();
const searchQuery = ref('');
const viewMode = ref<'grid' | 'list'>('grid');

const filteredApps = computed(() => {
  if (!searchQuery.value) return apps.value;
  return apps.value.filter(
    app =>
      app.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      app.des?.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

onMounted(() => {
  loadApps();
});

async function loadApps() {
  loading.value = true;
  try {
    const result = await getList({});
    if (result && typeof result === 'object') {
      const data = (result as any).result;
      apps.value = Array.isArray(data) ? data : [];
    }
  } catch (error) {
    console.error('加载应用列表失败:', error);
    ElMessage.error('加载应用列表失败');
    apps.value = [];
  } finally {
    loading.value = false;
  }
}

function handleCreate() {
  editModalRef.value?.open();
}

function handleEdit(app: any) {
  editModalRef.value?.open(app);
}

async function handleDelete(app: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除应用 "${app.name}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await del(app.id);
    ElMessage.success('删除成功');
    await loadApps();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
    }
  }
}

function handleView(app: any) {
  router.push(`/aigc/app/${app.id}`);
}

function getModelDisplay(app: any) {
  return app.model?.model || app.model?.name || '未配置';
}

const appIcons = [DataAnalysis, Connection, Operation];

function getAppIcon(index: number) {
  return appIcons[index % appIcons.length];
}

const appColors = [
  '#6366F1',
  '#8B5CF6',
  '#EC4899',
  '#F59E0B',
  '#10B981',
  '#3B82F6'
];

function getAppColor(index: number) {
  return appColors[index % appColors.length];
}
</script>

<template>
  <div class="view-container app-management bg-bg_color">
    <!-- 顶部操作栏 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div
        class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
      >
        <!-- 左侧标题 -->
        <div class="header-info">
          <h1 class="page-title flex items-center gap-2">
            <el-icon class="title-icon" :size="28" color="#6366F1">
              <DataAnalysis />
            </el-icon>
            应用管理中心
          </h1>
          <p class="page-subtitle">创建和管理您的 AI 应用，配置智能对话能力</p>
        </div>

        <!-- 右侧操作区 -->
        <div class="header-actions flex flex-wrap items-center gap-3">
          <!-- 搜索框 -->
          <div class="search-wrapper">
            <el-input
              v-model="searchQuery"
              placeholder="搜索应用..."
              :prefix-icon="Search"
              clearable
              class="search-input"
            />
          </div>

          <!-- 视图切换 -->
          <el-button-group class="view-toggle">
            <el-button
              :type="viewMode === 'grid' ? 'primary' : 'default'"
              :icon="Grid"
              @click="viewMode = 'grid'"
            />
            <el-button
              :type="viewMode === 'list' ? 'primary' : 'default'"
              :icon="List"
              @click="viewMode = 'list'"
            />
          </el-button-group>

          <!-- 刷新按钮 -->
          <el-button :icon="Refresh" :loading="loading" @click="loadApps">
            刷新
          </el-button>

          <!-- 创建按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            创建应用
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content flex-1 px-4 sm:px-6 lg:px-8 pb-4 overflow-auto">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="app-grid">
          <div
            v-for="i in 6"
            :key="`skeleton-${i}`"
            class="app-card skeleton-card"
          >
            <el-skeleton :rows="3" animated>
              <template #template>
                <div class="skeleton-header">
                  <el-skeleton-item variant="circle" class="skeleton-icon" />
                </div>
                <div class="skeleton-body">
                  <el-skeleton-item variant="h3" style="width: 60%" />
                  <el-skeleton-item variant="text" />
                  <el-skeleton-item variant="text" style="width: 80%" />
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
      </div>

      <!-- 应用网格视图 -->
      <div
        v-else-if="viewMode === 'grid' && filteredApps.length > 0"
        class="app-grid"
      >
        <div
          v-for="(app, index) in filteredApps"
          :key="app.id"
          class="app-card"
          :class="{ 'card-hover': true }"
          @click="handleView(app)"
        >
          <!-- 卡片图标区 -->
          <div
            class="card-icon-wrapper"
            :style="{
              background: `linear-gradient(135deg, ${getAppColor(index)}20, ${getAppColor(index)}10)`
            }"
          >
            <el-icon
              :size="36"
              :color="getAppColor(index)"
              class="card-main-icon"
            >
              <component :is="getAppIcon(index)" />
            </el-icon>

            <!-- 操作按钮组 -->
            <div class="card-actions">
              <el-button
                circle
                size="small"
                :icon="Edit"
                class="action-btn"
                @click.stop="handleEdit(app)"
              />
              <el-button
                circle
                size="small"
                :icon="Delete"
                class="action-btn delete-btn"
                @click.stop="handleDelete(app)"
              />
            </div>
          </div>

          <!-- 卡片内容 -->
          <div class="card-content">
            <h3 class="card-title">{{ app.name }}</h3>
            <p class="card-description">
              {{ app.des || '暂无描述' }}
            </p>

            <!-- 卡片标签 -->
            <div class="card-tags">
              <el-tag size="small" type="info" class="model-tag">
                <el-icon class="tag-icon"><Connection /></el-icon>
                {{ getModelDisplay(app) }}
              </el-tag>
            </div>

            <!-- 卡片底部信息 -->
            <div class="card-footer">
              <span class="create-time"
                >创建于 {{ app.createTime?.split(' ')[0] || '-' }}</span
              >
            </div>
          </div>
        </div>
      </div>

      <!-- 应用列表视图 -->
      <div
        v-else-if="viewMode === 'list' && filteredApps.length > 0"
        class="app-list"
      >
        <div
          v-for="(app, index) in filteredApps"
          :key="app.id"
          class="list-item"
          @click="handleView(app)"
        >
          <div
            class="list-item-icon"
            :style="{
              background: `linear-gradient(135deg, ${getAppColor(index)}20, ${getAppColor(index)}10)`
            }"
          >
            <el-icon :size="24" :color="getAppColor(index)">
              <component :is="getAppIcon(index)" />
            </el-icon>
          </div>

          <div class="list-item-content">
            <div class="list-item-header">
              <h3 class="list-item-title">{{ app.name }}</h3>
              <el-tag size="small" type="info">
                {{ getModelDisplay(app) }}
              </el-tag>
            </div>
            <p class="list-item-description">
              {{ app.des || '暂无描述' }}
            </p>
          </div>

          <div class="list-item-actions">
            <el-button text :icon="Edit" @click.stop="handleEdit(app)">
              编辑
            </el-button>
            <el-button
              text
              :icon="Delete"
              class="delete-text-btn"
              @click.stop="handleDelete(app)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredApps.length === 0" class="empty-state">
        <div class="empty-content">
          <div class="empty-icon-wrapper">
            <el-icon class="empty-icon" :size="64" color="#C0C4CC">
              <DataAnalysis />
            </el-icon>
          </div>
          <h3 class="empty-title">
            {{ searchQuery ? '没有找到匹配的应用' : '暂无应用' }}
          </h3>
          <p class="empty-description">
            {{
              searchQuery
                ? '请尝试其他搜索关键词'
                : '创建您的第一个 AI 应用，开启智能对话之旅'
            }}
          </p>
          <el-button
            v-if="!searchQuery"
            type="primary"
            :icon="Plus"
            size="large"
            class="empty-action-btn"
            @click="handleCreate"
          >
            创建应用
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑模态框 -->
    <AppEditModal ref="editModalRef" @success="loadApps" />
  </div>
</template>

<style lang="scss" scoped>
.app-management {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

// 头部样式
.header-section {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-info {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 8px;

    .title-icon {
      filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.2));
    }
  }

  .page-subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.header-actions {
  .search-wrapper {
    .search-input {
      width: 240px;
    }
  }

  .view-toggle {
    :deep(.el-button) {
      padding: 8px 12px;
    }
  }
}

// 主内容区域
.main-content {
  background: #f5f7fa;
  padding-top: 20px;
}

// 网格布局
.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

// 应用卡片
.app-card {
  background: white;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;

  &.card-hover:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .card-actions {
      opacity: 1;
    }
  }

  .card-icon-wrapper {
    position: relative;
    padding: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 120px;

    .card-main-icon {
      filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
    }

    .card-actions {
      position: absolute;
      top: 12px;
      right: 12px;
      display: flex;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.3s;

      .action-btn {
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        &:hover {
          transform: scale(1.1);
        }

        &.delete-btn:hover {
          color: #f56c6c;
          border-color: #f56c6c;
        }
      }
    }
  }

  .card-content {
    padding: 20px;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .card-description {
      font-size: 14px;
      color: #909399;
      line-height: 1.6;
      margin: 0 0 16px 0;
      height: 44px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .card-tags {
      margin-bottom: 12px;

      .model-tag {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        border-radius: 6px;

        .tag-icon {
          font-size: 12px;
        }
      }
    }

    .card-footer {
      padding-top: 12px;
      border-top: 1px solid #f0f2f5;

      .create-time {
        font-size: 12px;
        color: #c0c4cc;
      }
    }
  }
}

// 骨架屏卡片
.skeleton-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;

  .skeleton-header {
    padding: 24px;
    background: linear-gradient(135deg, #f5f7fa, #fafbfc);
    display: flex;
    justify-content: center;
    min-height: 120px;

    .skeleton-icon {
      width: 48px;
      height: 48px;
    }
  }

  .skeleton-body {
    padding: 20px;
  }
}

// 列表视图
.app-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .list-item {
    background: white;
    border-radius: 12px;
    padding: 16px 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    &:hover {
      transform: translateX(4px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    .list-item-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .list-item-content {
      flex: 1;
      min-width: 0;

      .list-item-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 4px;
      }

      .list-item-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }

      .list-item-description {
        font-size: 14px;
        color: #909399;
        margin: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .list-item-actions {
      display: flex;
      gap: 8px;

      .delete-text-btn {
        color: #f56c6c;
      }
    }
  }
}

// 空状态
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;

  .empty-content {
    text-align: center;
    max-width: 400px;

    .empty-icon-wrapper {
      width: 120px;
      height: 120px;
      margin: 0 auto 24px;
      background: linear-gradient(135deg, #f5f7fa, #fafbfc);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .empty-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    .empty-description {
      font-size: 14px;
      color: #909399;
      margin: 0 0 24px 0;
      line-height: 1.6;
    }

    .empty-action-btn {
      min-width: 120px;
    }
  }
}

// 响应式优化
@media (max-width: 640px) {
  .header-section {
    .header-actions {
      width: 100%;

      .search-wrapper {
        flex: 1;

        .search-input {
          width: 100%;
        }
      }
    }
  }
}
</style>
