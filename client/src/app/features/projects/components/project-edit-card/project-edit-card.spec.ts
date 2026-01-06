import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectEditCard } from './project-edit-card';

describe('ProjectEditCard', () => {
  let component: ProjectEditCard;
  let fixture: ComponentFixture<ProjectEditCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectEditCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectEditCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
