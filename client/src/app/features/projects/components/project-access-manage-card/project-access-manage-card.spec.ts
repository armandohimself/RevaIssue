import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAccessManageCard } from './project-access-manage-card';

describe('ProjectAccessManageCard', () => {
  let component: ProjectAccessManageCard;
  let fixture: ComponentFixture<ProjectAccessManageCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectAccessManageCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectAccessManageCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
