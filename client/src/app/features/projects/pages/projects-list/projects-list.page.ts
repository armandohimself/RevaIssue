import { Component, inject } from '@angular/core';
import { ProjectCardComponent } from '../../components/project-card/project-card.component';
import { ProjectsApi } from '../../data-access/projects.api';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-projects-list-page',
  standalone: true,
  imports: [ProjectCardComponent, CommonModule],
  templateUrl: './projects-list.page.html',
  styleUrl: './projects-list.page.css',
})
export class ProjectsListPage {
  private projectsApi = inject(ProjectsApi);

  projects$ = this.projectsApi.list();

  constructor() {
    this.projects$.subscribe({
      next: (projects) => console.log('Projects from backend:', projects),
      error: (err) => console.error('Backend error:', err),
    });
  }
}
