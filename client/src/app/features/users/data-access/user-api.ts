import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CreateUserRequest {
  userName: string;
  password: string;
  role: 'TESTER' | 'DEVELOPER';
}

export interface User {
  userId: string;
  userName: string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserApiService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
  }

  getUsersByProjectId(projectId: string): Observable<User[]> {
    return this.http.get<User[]>(`/api/projects/${projectId}/access/all`);
  }

  createUser(request: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/create`, request);
  }

  deleteUser(userId: string): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${userId}`, {
      responseType: 'text'
    });
  }

  getUserById(userId: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`);
  }
}
