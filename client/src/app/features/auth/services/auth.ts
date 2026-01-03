import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthApiService } from '../data-access/auth-api';
import { JwtStorage } from '../../../services/jwt-storage';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  
  constructor(
    private authApi: AuthApiService,
    private jwtStorage: JwtStorage,
    private router: Router
  ) {}

  login (username: string, password: string): void {
    this.authApi.login(username, password).subscribe({
      next: (response) => {
        // store the token
        this.jwtStorage.setToken(response.token);

        // get user info (token auto-attached by interceptor)
        this.authApi.getCurrentUser().subscribe({
          next: (user) => {
            // route based on the role
            this.routeByRole(user.role);
          },
          error: (error) => {
            console.error('Failed to get user info:', error);
            this.jwtStorage.clearToken();
          }
        });
      },
      error: (error) => {
        console.error('Login failed:', error);
      }
    });
  }

  private routeByRole(role: string): void {
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'TESTER':
        this.router.navigate(['/tester/dashboard']);
        break;
      case 'DEVELOPER':
        this.router.navigate(['/developer/dashboard']);
        break;
      default:
        this.router.navigate(['/login']);
    }
  }

  logout(): void {
    this.jwtStorage.clearToken();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.jwtStorage.getToken();
  }
}
