import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TemperatureBoilingComponent } from './temperature-boiling.component';

describe('TemperatureBoilingComponent', () => {
  let component: TemperatureBoilingComponent;
  let fixture: ComponentFixture<TemperatureBoilingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TemperatureBoilingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TemperatureBoilingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
