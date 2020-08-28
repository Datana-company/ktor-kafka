import { TestBed } from '@angular/core/testing';

import { TemperatureViewService } from './temperature-view.service';

describe('TemperatureViewService', () => {
  let service: TemperatureViewService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TemperatureViewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
