import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsEditPage } from './projects-edit.page';

describe('ProjectsEditPage', () => {
  let component: ProjectsEditPage;
  let fixture: ComponentFixture<ProjectsEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectsEditPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectsEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
