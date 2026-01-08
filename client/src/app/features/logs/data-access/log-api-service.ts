import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface LogTransaction {
  logId: number;
  message: string;
  actingUser: {
    userId: string;
    userName: string;
  };
  affectedEntityType: string;
  affectedEntityId: string;
  date: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root',
})
export class LogApiService {
  private apiUrl = 'http://localhost:8081/api/logs';

  constructor(private http: HttpClient) {}

  getAllLogs(page: number, size: number): Observable<Page<LogTransaction>> {
    return this.http.get<Page<LogTransaction>>(`${this.apiUrl}/get-all`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  getIssueHistory(issueId: string, page: number, size: number): Observable<Page<LogTransaction>> {
    return this.http.get<Page<LogTransaction>>(`${this.apiUrl}/issue/${issueId}`, {
      params: {page: page.toString(), size: size.toString() }
    });
  }


}
