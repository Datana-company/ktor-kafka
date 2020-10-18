import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MockListItemComponent } from './mock-list-item.component';

describe('MockListItemComponent', () => {
  let component: MockListItemComponent;
  let fixture: ComponentFixture<MockListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MockListItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
