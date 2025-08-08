// Dashboard Generator Types

export interface DashboardConfig {
  purpose: string;
  layout: string;
  theme: string;
  components: string[];
}

export interface ComponentDataConfig {
  componentId: string;
  componentType: string;
  dataSource?: string;
  refreshInterval?: number;
  
  // 图表类配置
  xField?: string;
  yField?: string;
  nameField?: string;
  valueField?: string;
  seriesField?: string;
  sampleData?: string;
  
  // KPI卡片配置
  title?: string;
  unit?: string;
  comparison?: 'chain' | 'year' | 'none';
  trend?: 'up' | 'down' | 'stable';
  
  // 表格配置
  columns?: Array<{
    field: string;
    title: string;
    width?: number;
    sortable?: boolean;
  }>;
  pagination?: boolean;
  pageSize?: number;
  
  // 自定义数据结构
  customSchema?: any;
}

// 主题配置（与后端 ThemeConfig 对应）
export interface ThemeConfig {
  name: string;
  colors: {
    primary: string;
    secondary: string;
    accent: string;
    background?: string;
    surface?: string;
    text?: string;
  };
}

// 仪表板请求（与后端 DashboardRequest 对应）
export interface DashboardRequest {
  purpose: string;
  purposeDetail?: string;
  focusMetrics?: string;
  customRequirements?: string;
  layout: string;
  theme: ThemeConfig;  // 发送给后端的是 ThemeConfig 对象
  components: string[];
  componentConfigs?: ComponentDataConfig[];
  options?: Record<string, any>;
}

export interface DashboardResponse {
  html: string;
  css: string;
  javascript: string;
  previewUrl?: string;
  metadata?: GenerationMetadata;
}

export interface GenerationMetadata {
  generatedAt: string;
  modelUsed: string;
  tokensUsed: number;
  generationTime: number;
}

export interface PurposeOption {
  id: string;
  icon: string;
  title: string;
  description: string;
  components: string[];
}

export interface LayoutOption {
  id: string;
  preview: string;
  title: string;
  description: string;
  responsive: boolean;
}

export interface ThemeOption {
  id: string;
  name: string;
  primary: string;
  secondary: string;
  accent: string;
  background: string;
  text: string;
}

export interface ComponentOption {
  id: string;
  icon: string;
  title: string;
  purposes: string[];
  previewCode: string;
}

export interface DashboardHistory {
  id: string;
  config: DashboardConfig;
  generatedHtml: string;
  createdAt: string;
  previewImage?: string;
}

// 文本常量接口定义
export interface AppTexts {
  app: AppMainTexts;
  wizard: WizardTexts;
  common: CommonTexts;
  messages: MessageTexts;
}

export interface AppMainTexts {
  title: string;
  subtitle: string;
  themeToggleTooltip: string;
  settingsTooltip: string;
  helpTooltip: string;
  footerText: string;
}

export interface WizardTexts {
  step1: Step1Texts;
  step2: Step2Texts;
  step3: Step3Texts;
  step4: Step4Texts;
}

export interface Step1Texts {
  title: string;
  description: string;
  purposeQuestion: string;
  purposes: Record<string, { title: string; description: string }>;
}

export interface Step2Texts {
  title: string;
  description: string;
  layoutQuestion: string;
  layouts: Record<string, { title: string; description: string }>;
}

export interface Step3Texts {
  title: string;
  description: string;
  themeQuestion: string;
  componentQuestion: string;
  themes: Record<string, { name: string; description: string }>;
  components: Record<string, { title: string; description: string }>;
}

export interface Step4Texts {
  title: string;
  description: string;
  generateButton: string;
  generatingButton: string;
  previewTitle: string;
  exportButton: string;
}

export interface CommonTexts {
  nextButton: string;
  prevButton: string;
  applyButton: string;
  editButton: string;
  saveButton: string;
  addButton: string;
  removeButton: string;
  resetButton: string;
  notSelected: string;
  countUnit: string;
}

export interface MessageTexts {
  success: Record<string, string>;
  error: Record<string, string>;
  info: Record<string, string>;
}
