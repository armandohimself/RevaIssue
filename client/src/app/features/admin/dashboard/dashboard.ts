import { Component, signal, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabGroup, MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AddUserComponent } from '../../users/add-user/add-user';
import { ListUsersComponent } from '../../users/list-users/list-users';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    AddUserComponent,
    ListUsersComponent
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class AdminDashboardComponent {
  selectedTabIndex = signal(0);

  // ref to MatTabGroup in template #tabGroup
  tabGroup = viewChild<MatTabGroup>('tabGroup');

  onTabChange(index: number): void {
    this.selectedTabIndex.set(index);
  }

  onUserCreated(): void {
    this.selectedTabIndex.set(1);
  }

  refreshUsers(): void {
    this.selectedTabIndex.set(1);
  }
}
