import { TestBed } from '@angular/core/testing';

import { Contenido } from './contenido';

describe('Contenido', () => {
  let service: Contenido;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Contenido);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
