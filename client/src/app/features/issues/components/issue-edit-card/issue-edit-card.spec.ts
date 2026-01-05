import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IssueEditCard } from './issue-edit-card';

describe('IssueEditCard', () => {
  let component: IssueEditCard;
  let fixture: ComponentFixture<IssueEditCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IssueEditCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IssueEditCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
