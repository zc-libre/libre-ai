import type { ComponentConfig } from '../types';

/**
 * 构建组件配置的工具函数
 */

// 构建图表组件的 specific 配置
export function buildChartSpecific(
  componentId: string,
  config: any
): Record<string, any> {
  const specific: Record<string, any> = {
    chartType: componentId.replace('-chart', '')
  };

  if (config.xField) specific.xField = config.xField;
  if (config.yField) specific.yField = config.yField;
  if (config.showLegend !== undefined) specific.showLegend = config.showLegend;
  if (config.showTooltip !== undefined)
    specific.showTooltip = config.showTooltip;
  if (config.showDataLabels !== undefined)
    specific.showDataLabels = config.showDataLabels;

  return specific;
}

// 构建饼图组件的 specific 配置
export function buildPieChartSpecific(config: any): Record<string, any> {
  const specific: Record<string, any> = {
    chartType: 'pie'
  };

  if (config.nameField) specific.nameField = config.nameField;
  if (config.valueField) specific.valueField = config.valueField;
  if (config.showLegend !== undefined) specific.showLegend = config.showLegend;

  return specific;
}

// 构建 KPI 卡片的 specific 配置
export function buildKPISpecific(config: any): Record<string, any> {
  const specific: Record<string, any> = {};

  if (config.title) specific.title = config.title;
  if (config.valueField) specific.valueField = config.valueField;
  if (config.unit) specific.unit = config.unit;

  if (config.comparisonType && config.comparisonType !== 'none') {
    specific.comparisonType = config.comparisonType;
  }

  if (config.showTrend) {
    specific.showTrend = true;
  }

  if (config.warningThreshold)
    specific.warningThreshold = config.warningThreshold;
  if (config.dangerThreshold) specific.dangerThreshold = config.dangerThreshold;

  return specific;
}

// 构建表格组件的 specific 配置
export function buildTableSpecific(config: any): Record<string, any> {
  const specific: Record<string, any> = {};

  if (config.columns) specific.columns = config.columns;
  if (config.pagination !== undefined) specific.pagination = config.pagination;
  if (config.pageSize) specific.pageSize = config.pageSize;

  if (config.tableFeatures?.length) {
    specific.tableFeatures = config.tableFeatures;
  }

  return specific;
}

// 尝试解析 JSON 字符串
export function tryParseJSON(jsonString: string): any {
  try {
    return JSON.parse(jsonString);
  } catch {
    return jsonString;
  }
}

// 构建完整的组件配置
export function buildComponentConfig(
  componentId: string,
  rawConfig: any
): ComponentConfig {
  // 构建基础配置
  const base = {
    componentId,
    componentType: componentId,
    dataSource: rawConfig.dataSource || '',
    refreshInterval: rawConfig.refreshInterval || 5000
  };

  // 构建数据映射配置
  const dataMapping = {
    sampleData: rawConfig.sampleData
      ? typeof rawConfig.sampleData === 'string'
        ? tryParseJSON(rawConfig.sampleData)
        : rawConfig.sampleData
      : null
  };

  // 根据组件类型构建 specific 配置
  let specific: Record<string, any> = {};

  if (componentId.includes('chart') && componentId !== 'pie-chart') {
    specific = buildChartSpecific(componentId, rawConfig);
  } else if (componentId === 'pie-chart') {
    specific = buildPieChartSpecific(rawConfig);
  } else if (componentId === 'kpi-card') {
    specific = buildKPISpecific(rawConfig);
  } else if (componentId === 'data-table') {
    specific = buildTableSpecific(rawConfig);
  }

  return {
    base,
    dataMapping,
    specific
  };
}
