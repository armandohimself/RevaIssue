import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

import { ProjectsApi } from '../../data-access/projects.api';
import { ProjectResponse } from '../../models/project.model';

@Component({
  selector: 'app-project-edit-card',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule],
  templateUrl: './project-edit-card.html',
  styleUrl: './project-edit-card.css',
})
export class ProjectEditCard {
 private projectsApi = inject(ProjectsApi);

  @Input({ required: true }) project!: ProjectResponse;

  @Output() closeCard = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  saving = signal(false);

  // editable fields
  name = signal('');
  desc = signal('');

  ngOnInit() {
    this.name.set(this.project.projectName ?? '');
    this.desc.set(this.project.projectDescription ?? '');
  }

  onClose() {
    this.closeCard.emit();
  }

  onSave() {
    const name = this.name().trim();
    if (!name) return alert('Project name is required.');

    this.saving.set(true);
    this.projectsApi.update(this.project.projectId, {
      projectName: name,
      projectDescription: this.desc().trim(),
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.saving.set(false);
        console.error('Update failed:', err);
        alert('Update failed.');
      },
    });
  }
}
