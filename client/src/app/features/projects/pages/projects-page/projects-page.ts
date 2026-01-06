import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { ProjectsApi } from '../../data-access/projects.api';
import { ProjectResponse, ProjectStatus } from '../../models/project.model';
import { ProjectCardComponent } from '../../components/project-card/project-card.component';
import { ProjectCreateCard } from './../../components/project-create-card/project-create-card';
import { ProjectEditCard } from './../../components/project-edit-card/project-edit-card';
import { ProjectAccessManageCard } from '../../components/project-access-manage-card/project-access-manage-card';


@Component({
  selector: 'app-projects-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,

    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,

    ProjectCardComponent,
    ProjectCreateCard,
    ProjectEditCard,
    ProjectAccessManageCard,
  ],
  templateUrl: './projects-page.html',
  styleUrl: './projects-page.css',
})
export class ProjectsPage {
  private projectsApi = inject(ProjectsApi);

  // data
  projects = signal<ProjectResponse[]>([]);
  loading = signal(true);

  // UI state
  searchText = signal('');
  statusFilter = signal<ProjectStatus | ''>('');
  showCreate = signal(false);

  editingProject = signal<ProjectResponse | null>(null);
  managingAccessFor = signal<ProjectResponse | null>(null);

  filteredProjects = computed(() => {
    const text = this.searchText().trim().toLowerCase();
    const status = this.statusFilter();

    return this.projects().filter((p) => {
      const matchesText =
        !text ||
        p.projectName.toLowerCase().includes(text) ||
        (p.projectDescription ?? '').toLowerCase().includes(text);

      const matchesStatus = !status || p.projectStatus === status;

      return matchesText && matchesStatus;
    });
  });

  constructor() {
    this.refresh();
  }

  refresh() {
    this.loading.set(true);
    this.projectsApi.list().subscribe({
      next: (rows) => {
        this.projects.set(rows);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Projects load failed:', err);
        this.loading.set(false);
      },
    });
  }

  onCreateClick() {
    this.showCreate.set(true);
  }

  onEdit(project: ProjectResponse) {
    this.editingProject.set(project);
  }

  onManageAccess(project: ProjectResponse) {
    this.managingAccessFor.set(project);
  }

  onArchive(project: ProjectResponse) {
    const ok = confirm(`Archive "${project.projectName}"?`);
    if (!ok) return;

    this.projectsApi.archive(project.projectId).subscribe({
      next: () => this.refresh(),
      error: (err) => console.error('Archive failed:', err),
    });
  }

  // overlay close handlers
  closeCreate() {
    this.showCreate.set(false);
  }

  closeEdit(refresh = false) {
    this.editingProject.set(null);
    if (refresh) this.refresh();
  }

  closeAccess() {
    this.managingAccessFor.set(null);
  }
}
