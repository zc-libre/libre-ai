import dayjs from 'dayjs';

export function formatToDateTime(
  date?: Date | string | number,
  format = 'YYYY-MM-DD HH:mm:ss'
): string {
  return dayjs(date).format(format);
}

export function formatToDate(
  date?: Date | string | number,
  format = 'YYYY-MM-DD'
): string {
  return dayjs(date).format(format);
}
