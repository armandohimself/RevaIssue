import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../services/user';

@Component({
  selector: 'app-add-user',
  imports: [FormsModule, CommonModule],
  templateUrl: './add-user.html',
  styleUrl: './add-user.css',
})
export class AddUserComponent {
  username = signal('');
  password = signal('');
  role = signal<'TESTER' | 'DEVELOPER' | ''>('');
  errorMessage = signal('');
  successMessage = signal('');
  isLoading = signal(false);

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.username() || !this.password() || !this.role()) {
      this.errorMessage.set("All fields are required");
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.userService.createUser({
      userName: this.username(),
      password: this.password(),
      role: this.role() as 'TESTER' | 'DEVELOPER'
    }).subscribe({
      next: (user) => {
        this.successMessage.set(`User ${user.userName} created successfully!`);
        this.isLoading.set(false);

        // waits 1 second
        setTimeout(() => {
          this.router.navigate(['/admin/users/get-all']);
        }, 1000); // this can be removed later but just to show
      },
      error: (error) => {
        console.error('Failed to create user:', error);
        this.errorMessage.set(error.error?.message || 'Failed to create user');
        this.isLoading.set(false);
      }
    });
  }
  resetForm(): void {
    this.username.set('');
    this.password.set('');
    this.role.set('');
  }
}
