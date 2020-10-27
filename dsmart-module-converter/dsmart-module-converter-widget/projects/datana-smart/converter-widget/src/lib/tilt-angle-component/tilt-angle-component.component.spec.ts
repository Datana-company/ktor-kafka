import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TiltAngleComponentComponent } from './tilt-angle-component.component';

describe('TiltAngleComponentComponent', () => {
  let component: TiltAngleComponentComponent;
  let fixture: ComponentFixture<TiltAngleComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TiltAngleComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TiltAngleComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
