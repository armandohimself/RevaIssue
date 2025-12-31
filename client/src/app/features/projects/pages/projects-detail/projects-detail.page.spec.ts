import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsDetailPage } from './projects-detail.page';

describe('ProjectsDetailPage', () => {
  let component: ProjectsDetailPage;
  let fixture: ComponentFixture<ProjectsDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectsDetailPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectsDetailPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
