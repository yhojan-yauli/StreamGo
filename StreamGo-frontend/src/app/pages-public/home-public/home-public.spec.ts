import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePublic } from './home-public';

describe('HomePublic', () => {
  let component: HomePublic;
  let fixture: ComponentFixture<HomePublic>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomePublic],
    }).compileComponents();

    fixture = TestBed.createComponent(HomePublic);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
