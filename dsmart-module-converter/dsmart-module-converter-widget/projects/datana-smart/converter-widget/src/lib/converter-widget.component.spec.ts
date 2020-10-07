import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConverterWidgetComponent } from './converter-widget.component';

describe('ConverterWidgetComponent', () => {
  let component: ConverterWidgetComponent;
  let fixture: ComponentFixture<ConverterWidgetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConverterWidgetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConverterWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
