import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthApiService } from '../data-access/auth-api';
import { JwtStorage } from '../../../services/jwt-storage';
import { Observable } from 'rxjs';
import { tap, switchMap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(
    private authApi: AuthApiService,
    private jwtStorage: JwtStorage,
    private router: Router
  ) {}

  login (username: string, password: string): Observable<any> {
    return this.authApi.login(username, password).pipe(
      tap((response) => {
        this.jwtStorage.setToken(response.token);
      }),
      switchMap(() => this.authApi.getCurrentUser()),
      catchError((error) => {
        this.jwtStorage.clearToken();
        throw error;
      })
    );
  }

  private routeByRole(role: string): void {
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'TESTER':
        this.router.navigate(['/user/dashboard']);
        break;
      case 'DEVELOPER':
        this.router.navigate(['/user/dashboard']);
        break;
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
