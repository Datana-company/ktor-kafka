import { TestBed } from '@angular/core/testing';

import { ConverterWidgetMockService } from './converter-widget-mock.service';

describe('ConverterWidgetMockService', () => {
  let service: ConverterWidgetMockService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConverterWidgetMockService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
