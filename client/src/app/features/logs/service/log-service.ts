import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LogApiService, LogTransaction, Page } from '../data-access/log-api-service';

@Injectable({
  providedIn: 'root',
})
export class LogService {

  constructor(private logApi: LogApiService) {}

  getAllLogs(page: number = 0, size: number = 20): Observable<Page<LogTransaction>> {
    return this.logApi.getAllLogs(page, size);
  }

  getIssueHistory(issueId: string, page: number = 0, size: number = 20): Observable<Page<LogTransaction>> {
    return this.logApi.getIssueHistory(issueId, page, size);
  }
}
