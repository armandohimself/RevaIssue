import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsCreatePage } from './projects-create.page';

describe('ProjectsCreatePage', () => {
  let component: ProjectsCreatePage;
  let fixture: ComponentFixture<ProjectsCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectsCreatePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectsCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
