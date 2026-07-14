import { HttpClient, HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ContenidoService {

  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/admin/contenidos`;
  private fileServerUrl = environment.fileServerUrl || 'https://portsmouth-think-integrity-exhibits.trycloudflare.com';

  // ============================================
  // MÉTODOS EXISTENTES (sin cambios)
  // ============================================

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  crear(contenido: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, contenido);
  }

  crearConArchivos(formData: FormData): Observable<HttpEvent<any>> {
    return this.http.post<any>(`${this.apiUrl}/crear-con-archivos`, formData, {
      reportProgress: true,
      observe: 'events',
    });
  }

  editar(id: number, contenido: any): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/${id}/editar`,
      contenido
    );
  }

  editarConArchivos(id: number, formData: FormData): Observable<HttpEvent<any>> {
    return this.http.put<any>(`${this.apiUrl}/${id}/editar-con-archivos`, formData, {
      reportProgress: true,
      observe: 'events',
    });
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/${id}`
    );
  }

  desactivar(id: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/${id}/desactivar`
    );
  }

  // ============================================
  // NUEVOS MÉTODOS: SUBIDA DIRECTA A CLOUDFLARE
  // ============================================

  /**
   * Sube un archivo directamente al servidor de archivos (Cloudflare)
   * SIN pasar por Render, evitando el timeout de 60 segundos.
   */
  async subirArchivoDirecto(file: File, subDir: string, nombreBase: string): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('subDir', subDir);
    formData.append('fileName', nombreBase);

    try {
      const response = await fetch(`${this.fileServerUrl}/api/files/upload`, {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Error ${response.status}: ${errorText}`);
      }

      const result = await response.json();
      console.log('✅ Archivo subido:', result.url);
      return result.url;
    } catch (error) {
      console.error('❌ Error en subida directa:', error);
      throw error;
    }
  }

  /**
   * Sanitiza un título para usarlo como nombre de archivo
   */
  private sanitizarTitulo(titulo: string): string {
    if (!titulo) return 'sin-titulo';
    return titulo.trim()
      .toLowerCase()
      .replace(/[\\/:*?"<>|]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '')
      || 'sin-titulo';
  }

  /**
   * Crea contenido subiendo archivos DIRECTAMENTE al servidor de archivos
   * y luego envía solo los datos a Render.
   */
  async crearConArchivosDirecto(
    contenidoData: any,
    imagen?: File,
    banner?: File,
    video?: File
  ): Promise<any> {
    const nombreBase = this.sanitizarTitulo(contenidoData.titulo);

    // 1. Subir archivos directamente al servidor de archivos
    const subidas = [];

    if (imagen) {
      subidas.push(this.subirArchivoDirecto(imagen, 'images', `${nombreBase}-poster`));
    } else {
      subidas.push(Promise.resolve(null));
    }

    if (banner) {
      subidas.push(this.subirArchivoDirecto(banner, 'images', `${nombreBase}-banner`));
    } else {
      subidas.push(Promise.resolve(null));
    }

    if (video) {
      subidas.push(this.subirArchivoDirecto(video, 'videos', nombreBase));
    } else {
      subidas.push(Promise.resolve(null));
    }

    const [imagenUrl, bannerUrl, videoUrl] = await Promise.all(subidas);

    // 2. Asignar URLs al objeto de datos
    const data = {
      ...contenidoData,
      imagenUrl: imagenUrl || contenidoData.imagenUrl || null,
      bannerUrl: bannerUrl || contenidoData.bannerUrl || null,
      videoUrl: videoUrl || contenidoData.videoUrl || null
    };

    // 3. Crear contenido en el backend (solo datos, sin archivos grandes)
    console.log('📤 Enviando datos a Render:', data);
    return lastValueFrom(this.http.post<any>(this.apiUrl, data));
  }

  /**
   * Actualiza contenido subiendo archivos DIRECTAMENTE al servidor de archivos
   */
  async editarConArchivosDirecto(
    id: number,
    contenidoData: any,
    imagen?: File,
    banner?: File,
    video?: File
  ): Promise<any> {
    const nombreBase = this.sanitizarTitulo(contenidoData.titulo);

    // 1. Subir archivos directamente al servidor de archivos
    const subidas = [];

    if (imagen) {
      subidas.push(this.subirArchivoDirecto(imagen, 'images', `${nombreBase}-poster`));
    } else {
      subidas.push(Promise.resolve(null));
    }

    if (banner) {
      subidas.push(this.subirArchivoDirecto(banner, 'images', `${nombreBase}-banner`));
    } else {
      subidas.push(Promise.resolve(null));
    }

    if (video) {
      subidas.push(this.subirArchivoDirecto(video, 'videos', nombreBase));
    } else {
      subidas.push(Promise.resolve(null));
    }

    const [imagenUrl, bannerUrl, videoUrl] = await Promise.all(subidas);

    // 2. Asignar URLs al objeto de datos
    const data = {
      ...contenidoData,
      imagenUrl: imagenUrl || contenidoData.imagenUrl || null,
      bannerUrl: bannerUrl || contenidoData.bannerUrl || null,
      videoUrl: videoUrl || contenidoData.videoUrl || null
    };

    // 3. Actualizar contenido en el backend (solo datos)
    console.log(`📤 Actualizando contenido ${id} en Render:`, data);
    return lastValueFrom(this.http.put<any>(`${this.apiUrl}/${id}/editar`, data));
  }
}
