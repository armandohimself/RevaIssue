import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TokenTransport {
  token: string;
}

export interface LoginRequest {
  userName: string;
  password: string;
}

export interface User {
  userId: string;
  userName: string;
  role: 'ADMIN' | 'TESTER' | 'DEVELOPER';
}

@Injectable({
  providedIn: 'root',
})
export class AuthApiService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  //login method
  login(username: string, password: string): Observable<TokenTransport> {
    const request: LoginRequest = {
      userName: username,
      password: password
    };
    return this.http.post<TokenTransport>(`${this.apiUrl}/login`, request);
  }

  //get current user
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }
}
