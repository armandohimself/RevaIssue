import { Component, signal, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../services/user';
import { UserStateService } from '../services/user-state';

@Component({
  selector: 'app-add-user',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
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
  userCreated = output<void>();

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private userStateService: UserStateService
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
        this.snackBar.open(`User ${user.userName} created successfully!`, 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        });
        this.isLoading.set(false);
        this.resetForm();

        // Trigger refresh
        this.userStateService.triggerRefresh();
        this.userCreated.emit(); // auto swap tabs
      },
      error: (error) => {
        console.error('Failed to create user:', error);
        this.errorMessage.set(error.error?.message || 'Failed to create user');
        this.snackBar.open('Failed to create user', 'Close', {
          duration: 5000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
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
