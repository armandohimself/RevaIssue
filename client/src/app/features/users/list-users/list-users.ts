import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../services/user';
import { User } from '../data-access/user-api';

@Component({
  selector: 'app-list-users',
  imports: [CommonModule],
  templateUrl: './list-users.html',
  styleUrl: './list-users.css',
})
export class ListUsersComponent implements OnInit {
  users = signal<User[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage.set('Failed to load users');
        this.isLoading.set(false);
      }
    });
  }

  deleteUser(userId: string): void {
    if (!confirm('Are you sure you want to delete this user?')) {
      return;
    }

    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to delete user:', error);
        this.errorMessage.set('Failed to delete user');
      }
    });
  }
}
