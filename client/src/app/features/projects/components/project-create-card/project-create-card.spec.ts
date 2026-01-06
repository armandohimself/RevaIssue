import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectCreateCard } from './project-create-card';

describe('ProjectCreateCard', () => {
  let component: ProjectCreateCard;
  let fixture: ComponentFixture<ProjectCreateCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectCreateCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectCreateCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
