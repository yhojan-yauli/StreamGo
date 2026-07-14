import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AlertService, AlertMessage } from './alert.service';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.html',
  styleUrl: './alert.scss',
})
export class AlertComponent implements OnInit, OnDestroy {
  alerts: (AlertMessage & { timer?: number })[] = [];
  private sub?: Subscription;

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    this.sub = this.alertService.alerts.subscribe(alert => {
      const item: AlertMessage & { timer?: number } = { ...alert, timer: undefined };
      this.alerts.push(item);
      item.timer = window.setTimeout(() => this.removeAlert(alert.id), 4000);
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.alerts.forEach(a => window.clearTimeout(a.timer));
  }

  removeAlert(id: number): void {
    const idx = this.alerts.findIndex(a => a.id === id);
    if (idx !== -1) {
      window.clearTimeout(this.alerts[idx].timer);
      this.alerts.splice(idx, 1);
    }
  }
}
