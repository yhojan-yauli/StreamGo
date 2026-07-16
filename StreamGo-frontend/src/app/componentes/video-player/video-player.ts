import { Component, Input, OnChanges, OnDestroy, inject, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { API_URL } from '../../services/api';

type PlayerType = 'youtube' | 'directo' | 'none';

@Component({
  selector: 'app-video-player',
  imports: [CommonModule],
  templateUrl: './video-player.html',
  styleUrl: './video-player.scss',
})
export class VideoPlayer implements OnChanges, OnDestroy {
  private sanitizer = inject(DomSanitizer);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild('videoPlayer') videoRef!: ElementRef<HTMLVideoElement>;

  @Input() videoUrl: string = '';
  @Input() titulo: string = '';

  tipo: PlayerType = 'none';
  safeUrl: SafeResourceUrl | null = null;
  videoUrlFinal: string = '';
  cargando = false;
  error = '';

  isPlaying = false;
  currentTime = 0;
  duration = 0;
  volume = 1;
  isMuted = false;
  controlsVisible = true;
  private hideTimer: any = null;
  private videoEl: HTMLVideoElement | null = null;

  bufferedPercent = 0;
  bufferedRanges: { start: number, end: number }[] = [];
  showQualityMenu = false;
  qualityLevels: string[] = ['Auto', '1080p', '720p', '480p', '360p'];
  currentQuality: string = 'Auto';
  private isUserSeeking = false;
  private qualityObserver: any = null;
  isPipActive = false;

  ngOnChanges(): void {
    this.iniciar();
  }

  ngOnDestroy(): void {
    this.limpiarVideo();
    if (this.qualityObserver) {
      this.qualityObserver.disconnect();
    }
  }

  private limpiarVideo(): void {
    if (this.videoEl) {
      this.videoEl.removeEventListener('loadedmetadata', this.onMetadata);
      this.videoEl.removeEventListener('timeupdate', this.onTimeUpdate);
      this.videoEl.removeEventListener('play', this.onPlay);
      this.videoEl.removeEventListener('pause', this.onPause);
      this.videoEl.removeEventListener('ended', this.onEnded);
      this.videoEl.removeEventListener('error', this.onError);
      this.videoEl.removeEventListener('progress', this.onProgress);
      this.videoEl.removeEventListener('waiting', this.onWaiting);
      this.videoEl.removeEventListener('canplay', this.onCanPlay);
      this.videoEl.removeEventListener('stalled', this.onStalled);
      this.videoEl = null;
    }
    if (this.hideTimer) clearTimeout(this.hideTimer);
  }

  private iniciar(): void {
    this.limpiarVideo();
    this.safeUrl = null;
    this.error = '';
    this.cargando = false;
    this.isPlaying = false;
    this.currentTime = 0;
    this.duration = 0;
    this.bufferedPercent = 0;
    this.bufferedRanges = [];
    this.videoUrlFinal = this.videoUrl.startsWith('/') ? `${API_URL}${this.videoUrl}` : this.videoUrl;

    if (!this.videoUrl) {
      this.tipo = 'none';
      this.cdr.detectChanges();
      return;
    }

    const embedUrl = this.obtenerEmbedUrl(this.videoUrl);

    if (embedUrl.startsWith('https://www.youtube.com/embed/')) {
      this.tipo = 'youtube';
      this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
    } else if (this.esVideoDirecto(this.videoUrlFinal)) {
      this.tipo = 'directo';
      this.cargando = true;
      this.iniciarObserverCalidad();
    } else {
      this.tipo = 'none';
      this.error = 'Formato de video no soportado.';
    }
    this.cdr.detectChanges();
  }

  private iniciarObserverCalidad(): void {
    if ('NetworkInformation' in navigator) {
      const connection = (navigator as any).connection;
      if (connection) {
        this.qualityObserver = connection.addEventListener('change', () => {
          this.adaptarCalidadPorConexion(connection);
        });
        this.adaptarCalidadPorConexion(connection);
      }
    }
  }

  private adaptarCalidadPorConexion(connection: any): void {
    if (this.currentQuality !== 'Auto') return;
    const speed = connection.downlink || 5;
    const rtt = connection.rtt || 100;
    if (speed > 20 && rtt < 50) {
      this.setQualityLevel('1080p');
    } else if (speed > 10 && rtt < 100) {
      this.setQualityLevel('720p');
    } else if (speed > 4) {
      this.setQualityLevel('480p');
    } else {
      this.setQualityLevel('360p');
    }
  }

  setQualityLevel(level: string): void {
    this.currentQuality = level;
    this.showQualityMenu = false;
    localStorage.setItem('preferredQuality', level);
    if (level === 'Auto') {
      if ('NetworkInformation' in navigator) {
        const connection = (navigator as any).connection;
        if (connection) {
          this.adaptarCalidadPorConexion(connection);
        }
      }
    }
  }

  toggleQualityMenu(): void {
    this.showQualityMenu = !this.showQualityMenu;
  }

  getCurrentQualityLabel(): string {
    return this.currentQuality;
  }

  onVideoInit(video: HTMLVideoElement): void {
    if (!video) return;
    this.videoEl = video;
    this.cargando = true;
    this.duration = video.duration || 0;
    video.addEventListener('loadedmetadata', this.onMetadata);
    video.addEventListener('timeupdate', this.onTimeUpdate);
    video.addEventListener('play', this.onPlay);
    video.addEventListener('pause', this.onPause);
    video.addEventListener('ended', this.onEnded);
    video.addEventListener('error', this.onError);
    video.addEventListener('progress', this.onProgress);
    video.addEventListener('waiting', this.onWaiting);
    video.addEventListener('canplay', this.onCanPlay);
    video.addEventListener('stalled', this.onStalled);
    video.volume = this.volume;
    const savedQuality = localStorage.getItem('preferredQuality');
    if (savedQuality && savedQuality !== 'Auto') {
      this.currentQuality = savedQuality;
    }
    this.cdr.detectChanges();
  }

  private onMetadata = (): void => {
    this.cargando = false;
    this.duration = this.videoEl?.duration || 0;
    this.cdr.detectChanges();
  };

  private onTimeUpdate = (): void => {
    if (!this.videoEl || this.isUserSeeking) return;
    this.currentTime = this.videoEl.currentTime;
    this.cdr.detectChanges();
  };

  private onPlay = (): void => {
    this.isPlaying = true;
    this.iniciarHideTimer();
    this.cdr.detectChanges();
  };

  private onPause = (): void => {
    this.isPlaying = false;
    if (this.hideTimer) clearTimeout(this.hideTimer);
    this.controlsVisible = true;
    this.cdr.detectChanges();
  };

  private onEnded = (): void => {
    this.isPlaying = false;
    this.controlsVisible = true;
    if (this.hideTimer) clearTimeout(this.hideTimer);
    this.cdr.detectChanges();
  };

  private onError = (): void => {
    this.cargando = false;
    this.error = 'No se pudo cargar el video.';
    this.cdr.detectChanges();
  };

  private onProgress = (): void => {
    if (!this.videoEl) return;
    const buffered = this.videoEl.buffered;
    this.bufferedRanges = [];
    for (let i = 0; i < buffered.length; i++) {
      this.bufferedRanges.push({
        start: buffered.start(i),
        end: buffered.end(i)
      });
    }
    if (buffered.length > 0) {
      const bufferedEnd = buffered.end(buffered.length - 1);
      this.bufferedPercent = (bufferedEnd / this.videoEl.duration) * 100;
      this.cdr.detectChanges();
    }
  };

  private onWaiting = (): void => {
    this.cargando = true;
    this.cdr.detectChanges();
  };

  private onCanPlay = (): void => {
    this.cargando = false;
    this.cdr.detectChanges();
  };

  private onStalled = (): void => {
    if (this.videoEl && this.videoEl.paused) {
      this.videoEl.play().catch(() => {});
    }
  };

  togglePlay(): void {
    if (!this.videoEl) return;
    if (this.videoEl.paused || this.videoEl.ended) {
      this.videoEl.play();
    } else {
      this.videoEl.pause();
    }
  }

  seek(event: Event): void {
    if (!this.videoEl) return;
    const target = event.target as HTMLInputElement;
    const value = Number(target.value);
    const time = (value / 100) * this.duration;
    this.isUserSeeking = true;
    this.videoEl.currentTime = time;
    setTimeout(() => { this.isUserSeeking = false; }, 100);
  }

  get progress(): number {
    if (!this.duration) return 0;
    return (this.currentTime / this.duration) * 100;
  }

  get bufferProgress(): number {
    return Math.min(this.bufferedPercent, 100);
  }

  getBufferedSegments(): { left: string, width: string }[] {
    if (!this.duration || this.bufferedRanges.length === 0) return [];
    return this.bufferedRanges.map(range => ({
      left: `${(range.start / this.duration) * 100}%`,
      width: `${((range.end - range.start) / this.duration) * 100}%`
    }));
  }

  setVolume(event: Event): void {
    if (!this.videoEl) return;
    const target = event.target as HTMLInputElement;
    this.volume = Number(target.value);
    this.videoEl.volume = this.volume;
    this.isMuted = this.volume === 0;
  }

  toggleMute(): void {
    if (!this.videoEl) return;
    this.isMuted = !this.isMuted;
    this.videoEl.muted = this.isMuted;
    if (this.isMuted) {
      this.volume = 0;
    } else {
      this.volume = this.videoEl.volume || 0.5;
      this.videoEl.volume = this.volume;
    }
  }

  toggleFullscreen(): void {
    const el = this.videoRef?.nativeElement?.parentElement;
    if (!el) return;
    if (document.fullscreenElement) {
      document.exitFullscreen();
    } else {
      el.requestFullscreen();
    }
  }

  togglePip(): void {
    if (!this.videoEl) return;
    if (document.pictureInPictureElement) {
      document.exitPictureInPicture();
      this.isPipActive = false;
    } else if (this.videoEl.requestPictureInPicture) {
      this.videoEl.requestPictureInPicture()
        .then(() => { this.isPipActive = true; })
        .catch(() => {});
    }
  }

  formatTime(seconds: number): string {
    if (!seconds || !isFinite(seconds)) return '0:00';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor(seconds % 60);
    if (h > 0) return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  showControls(): void {
    this.controlsVisible = true;
    if (this.hideTimer) clearTimeout(this.hideTimer);
    if (this.isPlaying) this.iniciarHideTimer();
  }

  hideControls(): void {
    if (this.isPlaying) {
      this.iniciarHideTimer();
    }
  }

  private iniciarHideTimer(): void {
    if (this.hideTimer) clearTimeout(this.hideTimer);
    this.hideTimer = setTimeout(() => {
      this.controlsVisible = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  private obtenerEmbedUrl(url: string): string {
    let id: string | null = null;
    const watchMatch = url.match(/(?:https?:\/\/)?(?:www\.)?youtube\.com\/watch\?v=([a-zA-Z0-9_-]+)/);
    if (watchMatch) id = watchMatch[1];
    if (!id) {
      const shortMatch = url.match(/(?:https?:\/\/)?youtu\.be\/([a-zA-Z0-9_-]+)/);
      if (shortMatch) id = shortMatch[1];
    }
    if (!id) {
      const shortsMatch = url.match(/(?:https?:\/\/)?(?:www\.)?youtube\.com\/shorts\/([a-zA-Z0-9_-]+)/);
      if (shortsMatch) id = shortsMatch[1];
    }
    if (!id) {
      const embedMatch = url.match(/(?:https?:\/\/)?(?:www\.)?youtube\.com\/embed\/([a-zA-Z0-9_-]+)/);
      if (embedMatch) id = embedMatch[1];
    }
    if (id) return `https://www.youtube.com/embed/${id}`;
    return url;
  }

  private esVideoDirecto(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv|m4v)(\?|$)/i.test(url);
  }
}