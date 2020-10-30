import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TiltAngleComponent } from './tilt-angle.component';

describe('TiltAngleComponentComponent', () => {
  let component: TiltAngleComponent;
  let fixture: ComponentFixture<TiltAngleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TiltAngleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TiltAngleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
