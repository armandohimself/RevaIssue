import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IssueCreateCard } from './issue-create-card';

describe('IssueCreateCard', () => {
  let component: IssueCreateCard;
  let fixture: ComponentFixture<IssueCreateCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IssueCreateCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IssueCreateCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
