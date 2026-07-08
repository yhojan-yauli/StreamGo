export interface Noticia {
  idPost: number;
  idAutor: number | null;
  autorNombre: string | null;
  idUsuario: number | null;
  usuarioNombre: string | null;
  titulo: string;
  reacciones: number | null;
  trailer: string | null;
  portadaUrl: string | null;
  contenido: string;
  fechaCreacion: string | null;
  fijado: boolean;
}

export interface NoticiaRequest {
  idAutor?: number | null;
  idUsuario?: number | null;
  titulo: string;
  reacciones: number;
  trailer: string;
  portadaUrl?: string | null;
  contenido: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface NoticiaQueryParams {
  search?: string;
  estado?: 'todos' | 'fijadas' | 'normales';
  sort?: 'recientes' | 'reacciones' | 'titulo';
  page?: number;
  size?: number;
}

export type NoticiaFiltro = 'todas' | 'destacadas' | 'populares' | 'recientes';

export interface NoticiaAdminFiltros {
  busqueda: string;
  estado: 'todos' | 'fijadas' | 'normales';
  orden: 'recientes' | 'reacciones' | 'titulo';
}
