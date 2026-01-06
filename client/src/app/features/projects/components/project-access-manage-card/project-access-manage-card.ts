import { Component, EventEmitter, Input, Output, computed, inject, signal } from '@angular/core';

import { ProjectAccessApi } from './../../data-access/projects-access.api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';


import { ProjectResponse } from '../../models/project.model';
import { ProjectAccessResponse, ProjectRole } from '../../models/project-access.model';
import { User } from '../../../users/data-access/user-api';
import { UserApiService } from '../../../users/data-access/user-api';

@Component({
  selector: 'app-project-access-manage-card',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule],
  templateUrl: './project-access-manage-card.html',
  styleUrl: './project-access-manage-card.css',
})
export class ProjectAccessManageCard {
private accessApi = inject(ProjectAccessApi);
  private userApi = inject(UserApiService);

  @Input({ required: true }) project!: ProjectResponse;

  @Output() closecard = new EventEmitter<void>();

  members = signal<ProjectAccessResponse[]>([]);
  users = signal<User[]>([]);
  loading = signal(true);

  selectedUserId = signal('');
  selectedRole = signal<ProjectRole>('DEVELOPER');

  // optional: only allow adding DEV/TESTER in UI
  eligibleUsers = computed(() => this.users().filter(u => u.role !== 'ADMIN'));

  ngOnInit() {
    this.refresh();
    this.userApi.getAllUsers().subscribe({
      next: (rows) => this.users.set(rows as any),
      error: (err) => console.error('Users load failed:', err),
    });
  }

  refresh() {
    this.loading.set(true);
    this.accessApi.list(this.project.projectId).subscribe({
      next: (rows) => {
        this.members.set(rows);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Access list failed:', err);
        this.loading.set(false);
      },
    });
  }

  onClose() {
    this.closecard.emit();
  }

  onGrant() {
    const userId = this.selectedUserId();
    if (!userId) return alert('Pick a user first.');

    this.accessApi.grant(this.project.projectId, {
      userId,
      projectRole: this.selectedRole(),
    }).subscribe({
      next: () => {
        this.selectedUserId.set('');
        this.refresh();
      },
      error: (err) => {
        console.error('Grant failed:', err);
        alert('Grant failed (maybe user already has access).');
      },
    });
  }

  onRevoke(userId: string) {
    const ok = confirm('Revoke access for this user?');
    if (!ok) return;

    this.accessApi.revoke(this.project.projectId, userId).subscribe({
      next: () => this.refresh(),
      error: (err) => {
        console.error('Revoke failed:', err);
        alert('Revoke failed.');
      },
    });
  }

  userName(userId: string) {
    return this.users().find(u => u.userId === userId)?.userName ?? userId;
  }
}
