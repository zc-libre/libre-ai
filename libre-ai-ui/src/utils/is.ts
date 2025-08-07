/**
 * 判断值是否为空或空白字符串
 * @param value 要检查的值
 * @returns 如果值为 null、undefined 或空白字符串则返回 true，否则返回 false
 */
export function isNullOrWhitespace(value: any): boolean {
  return value == null || (typeof value === 'string' && value.trim() === '');
}

/**
 * 判断值是否为 null 或 undefined
 * @param value 要检查的值
 * @returns 如果值为 null 或 undefined 则返回 true，否则返回 false
 */
export function isNullOrUndefined(value: any): boolean {
  return value == null;
}

/**
 * 判断值是否为 null 或 undefined (简化别名)
 * @param value 要检查的值
 * @returns 如果值为 null 或 undefined 则返回 true，否则返回 false
 */
export function isNullOrUnDef(value: any): boolean {
  return value == null;
}

/**
 * 判断值是否为字符串
 * @param value 要检查的值
 * @returns 如果值为字符串则返回 true，否则返回 false
 */
export function isString(value: any): value is string {
  return typeof value === 'string';
}

/**
 * 判断值是否为数字
 * @param value 要检查的值
 * @returns 如果值为数字则返回 true，否则返回 false
 */
export function isNumber(value: any): value is number {
  return typeof value === 'number' && !isNaN(value);
}

/**
 * 判断值是否为布尔值
 * @param value 要检查的值
 * @returns 如果值为布尔值则返回 true，否则返回 false
 */
export function isBoolean(value: any): value is boolean {
  return typeof value === 'boolean';
}

/**
 * 判断值是否为数组
 * @param value 要检查的值
 * @returns 如果值为数组则返回 true，否则返回 false
 */
export function isArray(value: any): value is any[] {
  return Array.isArray(value);
}

/**
 * 判断值是否为对象
 * @param value 要检查的值
 * @returns 如果值为对象则返回 true，否则返回 false
 */
export function isObject(value: any): value is object {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

/**
 * 判断值是否为函数
 * @param value 要检查的值
 * @returns 如果值为函数则返回 true，否则返回 false
 */
export function isFunction(value: any): value is Function {
  return typeof value === 'function';
}
