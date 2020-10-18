import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigatePanelComponent } from './navigate-panel.component';

describe('NavigatePanelComponent', () => {
  let component: NavigatePanelComponent;
  let fixture: ComponentFixture<NavigatePanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NavigatePanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigatePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
