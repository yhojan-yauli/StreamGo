import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ClientePlanesService } from '../../services/cliente-planes';
import { PagosService } from '../../services/pagos';

@Component({ selector: 'app-planes-client', imports: [CommonModule, FormsModule, NavbarClient], templateUrl: './planes-client.html', styleUrl: './planes-client.scss' })
export class PlanesClient implements OnInit {
  private planesService = inject(ClientePlanesService); private pagosService = inject(PagosService); private cdr = inject(ChangeDetectorRef);
  planes: any[] = []; miSuscripcion: any = null; planSeleccionado: any = null; planPersonalizado: any = null; pagoRespuesta: any = null; metodoPago = 'YAPE';
  precioPersonalizado = 9; cargando = false; pagando = false; cargandoPersonalizado = false; mensaje = ''; error = '';
  ngOnInit(): void { this.cargarPlanes(); this.cargarMiSuscripcion(); }
  cargarPlanes(): void { this.cargando = true; this.planesService.listarPlanes().subscribe({ next: data => { this.planes = Array.isArray(data) ? data.filter(plan => plan.activo !== false && !plan.personalizado) : []; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error('Error cargando planes:', err); this.planes = []; this.cargando = false; this.error = 'No se pudieron cargar los planes.'; this.cdr.detectChanges(); } }); }
  cargarMiSuscripcion(): void { this.planesService.miSuscripcion().subscribe({ next: data => { this.miSuscripcion = data; this.cdr.detectChanges(); }, error: () => { this.miSuscripcion = null; this.cdr.detectChanges(); } }); }
  verificarSuscripcion(): void { this.planesService.verificarSuscripcion().subscribe({ next: data => { this.miSuscripcion = data; this.mensaje = 'Suscripción verificada correctamente.'; this.cdr.detectChanges(); }, error: err => { console.error(err); this.error = 'No se pudo verificar la suscripción.'; this.cdr.detectChanges(); } }); }
  seleccionarPlan(plan: any): void { this.planSeleccionado = plan; this.pagoRespuesta = null; this.mensaje = ''; this.error = ''; this.cdr.detectChanges(); }
  cerrarPago(): void { this.planSeleccionado = null; this.pagoRespuesta = null; this.mensaje = ''; this.error = ''; this.cdr.detectChanges(); }
  pagarPlan(): void { if (!this.planSeleccionado?.id) { this.error = 'Selecciona un plan válido.'; return; } this.pagando = true; this.error = ''; this.mensaje = ''; this.pagosService.crearPago(this.planSeleccionado.id, this.metodoPago).subscribe({ next: data => { this.pagoRespuesta = data; this.mensaje = data?.mensaje || 'Pago creado correctamente.'; this.pagando = false; this.cargarMiSuscripcion(); this.cdr.detectChanges(); }, error: err => { console.error(err); this.error = 'No se pudo crear el pago.'; this.pagando = false; this.cdr.detectChanges(); } }); }
  onPrecioChange(): void { this.cargandoPersonalizado = true; this.planPersonalizado = null; this.planesService.obtenerPlanPersonalizado(this.precioPersonalizado).subscribe({ next: data => { this.planPersonalizado = data; this.cargandoPersonalizado = false; this.cdr.detectChanges(); }, error: err => { console.error(err); this.cargandoPersonalizado = false; this.error = 'No se pudo calcular el plan personalizado.'; this.cdr.detectChanges(); } }); }
  estadoSuscripcion(): string { return this.miSuscripcion?.estado || 'SIN SUSCRIPCIÓN'; }
  horasRestantes(): number { return this.miSuscripcion?.horasRestantes || 0; }
}
