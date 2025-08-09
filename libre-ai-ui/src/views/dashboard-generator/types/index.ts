// Dashboard Generator Types

export interface DashboardConfig {
  purpose: string;
  layout: string;
  theme: string;
  components: string[];
}

// ============= 组件配置分层结构 =============

// 基础配置（所有组件通用）
export interface BaseComponentConfig {
  componentId: string;
  componentType: string;
  dataSource?: string;
  refreshInterval?: number;
}

// 数据映射配置
export interface DataMappingConfig {
  sampleData?: any;
}

// 组件配置结构（分层设计）
export interface ComponentConfig {
  base: BaseComponentConfig;
  dataMapping: DataMappingConfig;
  specific: Record<string, any>; // 根据组件类型动态确定
}

// 主题配置（与后端 ThemeConfig 对应）
export interface ThemeConfig {
  name: string;
  colors: {
    primary: string;
    secondary: string;
    accent: string;
  };
}

// 仪表板请求（与后端 DashboardRequest 对应）
export interface DashboardRequest {
  purpose: string;
  purposeDetail?: string;
  focusMetrics?: string;
  customRequirements?: string;
  layout: string;
  theme: ThemeConfig;
  components: string[];
  componentConfigs?: ComponentConfig[];
  options?: {
    responsive?: boolean;
    includeData?: boolean;
    additionalRequirements?: string;
  };
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
