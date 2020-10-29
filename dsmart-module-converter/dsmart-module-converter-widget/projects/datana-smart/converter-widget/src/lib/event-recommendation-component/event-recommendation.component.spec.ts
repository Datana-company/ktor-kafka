import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventRecommendationComponent } from './event-recommendation.component';

describe('EventRecommendationComponentComponent', () => {
  let component: EventRecommendationComponent;
  let fixture: ComponentFixture<EventRecommendationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventRecommendationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventRecommendationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
