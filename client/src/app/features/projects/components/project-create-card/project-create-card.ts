import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { ProjectsApi } from '../../data-access/projects.api';

@Component({
  selector: 'app-project-create-card',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule],
  templateUrl: './project-create-card.html',
  styleUrl: './project-create-card.css',
})
export class ProjectCreateCard {
private projectsApi = inject(ProjectsApi);

  @Output() closecard = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  projectName = signal('');
  projectDescription = signal('');
  saving = signal(false);

  onClose() {
    this.closecard.emit();
  }

  onCreate() {
    const name = this.projectName().trim();
    if (!name) return alert('Project name is required.');

    this.saving.set(true);
    this.projectsApi.create({
      projectName: name,
      projectDescription: this.projectDescription().trim(),
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.created.emit();
      },
      error: (err) => {
        this.saving.set(false);
        console.error('Create project failed:', err);
        alert('Create failed.');
      },
    });
  }
}
