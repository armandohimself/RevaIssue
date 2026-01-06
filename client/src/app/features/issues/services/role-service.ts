import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { JwtStorage } from '../../../services/jwt-storage';
export type UserRole = 'ADMIN' | 'TESTER' | 'DEVELOPER';

interface User {
  userId: string;
  userName: string;
  role: UserRole;
}

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  private roleSubject = new BehaviorSubject<UserRole | null>(null);
  private userIdSubject = new BehaviorSubject<string | null>(null);
  private apiUrl = 'http://localhost:8081/api/users/me';

  constructor(private http: HttpClient, private jwtStorage: JwtStorage) {}

  fetchUserRole() {
    this.http.get<User>(this.apiUrl).subscribe({
      next: (user) => {
        console.log('User role fetched:', user.role);
        this.roleSubject.next(user.role);
        this.userIdSubject.next(user.userId);
      },
      error: (err) => {
        console.log(err);
        this.roleSubject.next(null);
        this.userIdSubject.next(null);
      }
    });
  }

  getUserRole(): UserRole | null {
    return this.roleSubject.getValue();
  }

  getUserId(): string | null {
    return this.userIdSubject.getValue();
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  isTester(): boolean {
    return this.getUserRole() === 'TESTER';
  }

  isDeveloper(): boolean {
    return this.getUserRole() === 'DEVELOPER';
  }

  canCreateIssue(): boolean {
    const role = this.getUserRole();
    return role === 'ADMIN' || role === 'TESTER';
  }

  canEditIssue(): boolean {
    const role = this.getUserRole();
    return role === 'ADMIN' || role === 'TESTER';
  }

  getAvailableStatuses(): string[] {
    const role = this.getUserRole();
    if (role === 'DEVELOPER') {
      return ['IN_PROGRESS', 'RESOLVED'];
    } else if (role === 'TESTER') {
      return ['OPEN', 'CLOSED'];
    }
    return ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
  }

  clearRole(): void {
    this.roleSubject.next(null);
    this.userIdSubject.next(null);
  }
}
