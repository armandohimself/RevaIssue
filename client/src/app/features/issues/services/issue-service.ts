import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Page } from '../../../interfaces/page';
import { IssueCreate, IssueData, IssueStatus, IssueUpdate, LogTransaction } from '../interfaces/issue-data';

@Injectable({
  providedIn: 'root',
})
export class IssueService {
  private issuesSubject: BehaviorSubject<IssueData[]>;
  private selectedIssueSubject: BehaviorSubject<IssueData | null>;

  constructor(private httpClient: HttpClient) {
    this.issuesSubject = new BehaviorSubject<IssueData[]>([]);
    this.selectedIssueSubject = new BehaviorSubject<IssueData | null>(null);
  }

  getIssuesSubject() {
    return this.issuesSubject;
  }

  getIssuesForProject(projectId: string) {
    this.httpClient.get<IssueData[]>(`api/projects/${projectId}/issues`)
      .subscribe({
        next: responseData => {
          console.log(responseData);
          this.issuesSubject.next(responseData);
        },
        error: err => {
          console.log(err);
          this.issuesSubject.next([]);
        }
      });
  }

  getIssueById(issueId: string) {
    this.httpClient.get<IssueData>(`api/issues/${issueId}`)
      .subscribe({
        next: responseData => {
          console.log(responseData);
          this.selectedIssueSubject.next(responseData);
        },
        error: err => {
          console.log(err);
          this.selectedIssueSubject.next(null);
        }
      });
  }

  getIssuesAssignedToUser(userId: string) {
    this.httpClient.get<IssueData[]>(`api/users/${userId}/assigned-issues`)
      .subscribe({
        next: responseData => {
          console.log(responseData);
          this.issuesSubject.next(responseData);
        },
        error: err => {
          console.log(err);
          this.issuesSubject.next([]);
        }
      });
  }

  createIssue(projectId: string, dto: IssueCreate) {
    this.httpClient.post<IssueData>(`api/projects/${projectId}/issues`, dto)
      .subscribe({
        next: responseData => {
          console.log(responseData);
          const currentIssues = this.issuesSubject.getValue();
          this.issuesSubject.next([...currentIssues, responseData]);
        },
        error: err => {
          console.log(err);
        }
      });
  }

  updateIssue(issueId: string, dto: IssueUpdate) {
    this.httpClient.put<IssueData>(`api/issues/${issueId}`, dto)
      .subscribe({
        next: responseData => {
          console.log(responseData);
          const currentIssues = this.issuesSubject.getValue();
          const updated = currentIssues.map(issue => 
            issue.issueId === issueId ? responseData : issue
          );
          this.issuesSubject.next(updated);
          this.selectedIssueSubject.next(responseData);
        },
        error: err => {
          console.log(err);
        }
      });
  }

  updateStatus(issueId: string, status: IssueStatus) {
    this.httpClient.put<IssueData>(`api/issues/${issueId}/status?status=${status}`, {})
      .subscribe({
        next: responseData => {
          console.log(responseData);
          const currentIssues = this.issuesSubject.getValue();
          const updated = currentIssues.map(issue => 
            issue.issueId === issueId ? responseData : issue
          );
          this.issuesSubject.next(updated);
        },
        error: err => {
          console.log(err);
        }
      });
  }

  deleteIssue(issueId: string) {
    this.httpClient.delete<void>(`api/issues/${issueId}`)
      .subscribe({
        next: () => {
          console.log('Issue deleted');
          const currentIssues = this.issuesSubject.getValue();
          const filtered = currentIssues.filter(issue => issue.issueId !== issueId);
          this.issuesSubject.next(filtered);
        },
        error: err => {
          console.log(err);
        }
      });
  }
  assignDeveloper(issueId: string, userId: string) {
    this.httpClient.put<IssueData>(`api/issues/${issueId}/assign/${userId}`, {})
      .subscribe({
        next: (responseData) => {
          console.log('User assigned to issue');
          const currentIssues = this.issuesSubject.getValue();
          const updated = currentIssues.map(issue => 
            issue.issueId === issueId ? responseData : issue
          );
          this.issuesSubject.next(updated);
        },
        error: err => {
          console.log(err);
        }
      });
  }

  getIssueHistory(issueId: string, page: number = 0, size: number = 20): Observable<Page<LogTransaction>> {
    return this.httpClient.get<Page<LogTransaction>>(`api/logs/issue/${issueId}?page=${page}&size=${size}`);
  }
}
