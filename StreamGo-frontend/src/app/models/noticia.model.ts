export interface Noticia {
  idPost: number;
  idAutor: number | null;
  autorNombre: string | null;
  idUsuario: number | null;
  usuarioNombre: string | null;
  titulo: string;
  reacciones: number | null;
  trailer: string | null;
  contenido: string;
  fijado: boolean;
}

export interface NoticiaRequest {
  idAutor: number;
  idUsuario: number;
  titulo: string;
  reacciones: number;
  trailer: string;
  contenido: string;
}

export type NoticiaFiltro = 'todas' | 'destacadas' | 'populares' | 'recientes';

export interface NoticiaAdminFiltros {
  busqueda: string;
  estado: 'todos' | 'fijadas' | 'normales';
  orden: 'recientes' | 'reacciones' | 'titulo';
}
