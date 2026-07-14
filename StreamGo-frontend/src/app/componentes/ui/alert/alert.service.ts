import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface AlertMessage {
  id: number;
  type: 'success' | 'error' | 'warning' | 'info';
  text: string;
}

@Injectable({ providedIn: 'root' })
export class AlertService {
  private alerts$ = new Subject<AlertMessage>();
  alerts = this.alerts$.asObservable();
  private counter = 0;

  success(text: string): void {
    this.emit('success', text);
  }
  error(text: string): void {
    this.emit('error', text);
  }
  warning(text: string): void {
    this.emit('warning', text);
  }
  info(text: string): void {
    this.emit('info', text);
  }

  private emit(type: AlertMessage['type'], text: string): void {
    this.alerts$.next({ id: ++this.counter, type, text });
  }

  confirm(text: string): Promise<boolean> {
    return new Promise(resolve => {
      const result = window.confirm(text);
      resolve(result);
    });
  }
}
