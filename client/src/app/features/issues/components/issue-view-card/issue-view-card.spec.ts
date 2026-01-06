import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IssueViewCard } from './issue-view-card';

describe('IssueViewCard', () => {
  let component: IssueViewCard;
  let fixture: ComponentFixture<IssueViewCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IssueViewCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IssueViewCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
