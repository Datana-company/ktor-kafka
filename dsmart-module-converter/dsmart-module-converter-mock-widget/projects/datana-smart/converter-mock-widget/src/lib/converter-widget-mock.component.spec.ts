import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConverterWidgetMockComponent } from './converter-widget-mock.component';

describe('ConverterWidgetMockComponent', () => {
  let component: ConverterWidgetMockComponent;
  let fixture: ComponentFixture<ConverterWidgetMockComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConverterWidgetMockComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConverterWidgetMockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
