import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAccessTableComponent } from './project-access-table.component';

describe('ProjectAccessTableComponent', () => {
  let component: ProjectAccessTableComponent;
  let fixture: ComponentFixture<ProjectAccessTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectAccessTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectAccessTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
