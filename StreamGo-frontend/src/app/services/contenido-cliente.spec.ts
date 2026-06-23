import { TestBed } from '@angular/core/testing';

import { ContenidoCliente } from './contenido-cliente';

describe('ContenidoCliente', () => {
  let service: ContenidoCliente;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContenidoCliente);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
