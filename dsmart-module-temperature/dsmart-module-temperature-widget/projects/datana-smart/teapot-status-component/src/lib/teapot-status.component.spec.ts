import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeapotStatusComponent } from './teapot-status.component';

describe('TeapotStatusComponent', () => {
  let component: TeapotStatusComponent;
  let fixture: ComponentFixture<TeapotStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeapotStatusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeapotStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
