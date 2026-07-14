import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Contenido } from './contenido';

describe('Contenido', () => {
  let component: Contenido;
  let fixture: ComponentFixture<Contenido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Contenido],
    }).compileComponents();

    fixture = TestBed.createComponent(Contenido);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
