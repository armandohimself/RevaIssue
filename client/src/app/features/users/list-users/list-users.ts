import { Component, OnInit, signal, viewChild, AfterViewInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { UserService } from '../services/user';
import { User } from '../data-access/user-api';
import { UserStateService } from '../services/user-state';

@Component({
  selector: 'app-list-users',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTooltipModule
  ],
  templateUrl: './list-users.html',
  styleUrl: './list-users.css',
})
export class ListUsersComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['userName', 'role', 'actions'];
  dataSource = new MatTableDataSource<User>([]);
  sort = viewChild<MatSort>(MatSort);

  isLoading = signal(false);
  errorMessage = signal('');

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private userStateService: UserStateService
  ) {
    // Listen for refresh signals
    effect(() => {
      this.userStateService.refreshNeeded();
      this.loadUsers();
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  ngAfterViewInit() {
    const sortInstance = this.sort();
    if (sortInstance) {
      this.dataSource.sort = sortInstance;
    }
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        console.log('Loaded users:', users); // Debug log
        this.dataSource.data = users;
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage.set('Failed to load users');
        this.snackBar.open('Failed to load users', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
        this.isLoading.set(false);
      }
    });
  }

  deleteUser(userId: string, userName: string): void {
    if (!confirm(`Are you sure you want to delete user "${userName}"?`)) {
      return;
    }

    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.snackBar.open(`User ${userName} deleted successfully`, 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        });
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to delete user:', error);
        this.snackBar.open('Failed to delete user', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  getRoleColor(role: string): string {
    switch (role) {
      case 'TESTER':
        return 'accent';
      case 'DEVELOPER':
        return 'primary';
      default:
        return '';
    }
  }
}
