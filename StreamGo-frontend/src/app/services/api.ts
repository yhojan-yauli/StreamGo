export const API_URL = 'http://localhost:8080';

export function urlCompleta(url: string | null | undefined): string {
  if (!url) return '/background.png';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  return `${API_URL}${url}`;
}
