export enum ModelTypeEnum {
  CHAT = 'CHAT',
  EMBEDDING = 'EMBEDDING',
  TEXT_IMAGE = 'TEXT_IMAGE',
  WEB_SEARCH = 'WEB_SEARCH'
}

export function hideKey(key: string): string {
  if (!key || key.length < 8) {
    return key;
  }
  const start = key.substring(0, 4);
  const end = key.substring(key.length - 4);
  return `${start}****${end}`;
}
