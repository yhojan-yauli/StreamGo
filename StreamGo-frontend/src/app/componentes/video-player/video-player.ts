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

  ngOnChanges(): void {
    this.iniciar();
  }

  ngOnDestroy(): void {
    this.limpiarVideo();
  }

  private limpiarVideo(): void {
    if (this.videoEl) {
      this.videoEl.removeEventListener('loadedmetadata', this.onMetadata);
      this.videoEl.removeEventListener('timeupdate', this.onTimeUpdate);
      this.videoEl.removeEventListener('play', this.onPlay);
      this.videoEl.removeEventListener('pause', this.onPause);
      this.videoEl.removeEventListener('ended', this.onEnded);
      this.videoEl.removeEventListener('error', this.onError);
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
    } else {
      this.tipo = 'none';
      this.error = 'Formato de video no soportado.';
    }
    this.cdr.detectChanges();
  }

  onVideoInit(video: HTMLVideoElement): void {
    if (!video) return;
    this.videoEl = video;
    this.cargando = false;
    this.duration = video.duration || 0;
    video.addEventListener('timeupdate', this.onTimeUpdate);
    video.addEventListener('play', this.onPlay);
    video.addEventListener('pause', this.onPause);
    video.addEventListener('ended', this.onEnded);
    video.addEventListener('error', this.onError);
    video.volume = this.volume;
    this.cdr.detectChanges();
  }

  private onMetadata = (): void => {
    this.cargando = false;
    this.duration = this.videoEl?.duration || 0;
    this.cdr.detectChanges();
  };

  private onTimeUpdate = (): void => {
    if (!this.videoEl) return;
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
    this.videoEl.currentTime = time;
  }

  get progress(): number {
    if (!this.duration) return 0;
    return (this.currentTime / this.duration) * 100;
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

    const watchMatch = url.match(
      /(?:https?:\/\/)?(?:www\.)?youtube\.com\/watch\?v=([a-zA-Z0-9_-]+)/
    );
    if (watchMatch) id = watchMatch[1];

    if (!id) {
      const shortMatch = url.match(
        /(?:https?:\/\/)?youtu\.be\/([a-zA-Z0-9_-]+)/
      );
      if (shortMatch) id = shortMatch[1];
    }

    if (!id) {
      const shortsMatch = url.match(
        /(?:https?:\/\/)?(?:www\.)?youtube\.com\/shorts\/([a-zA-Z0-9_-]+)/
      );
      if (shortsMatch) id = shortsMatch[1];
    }

    if (!id) {
      const embedMatch = url.match(
        /(?:https?:\/\/)?(?:www\.)?youtube\.com\/embed\/([a-zA-Z0-9_-]+)/
      );
      if (embedMatch) id = embedMatch[1];
    }

    if (id) return `https://www.youtube.com/embed/${id}`;

    return url;
  }

  private esVideoDirecto(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv|m4v)(\?|$)/i.test(url);
  }
}
