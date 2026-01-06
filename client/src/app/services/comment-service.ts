import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Comment } from '../interfaces/comment';
import { Page } from '../interfaces/page';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  constructor(private http: HttpClient) {}

  getCommentsByIssueId(
    issueId: string,
    page: number = 0,
    size: number = 10
  ): Observable<Page<Comment>> {
    return this.http.get<Page<Comment>>(`comments/issue/${issueId}`, {
      params: { page, size, sort: 'time,asc' },
    });
  }

  getCommentsByUserId(
    userId: string,
    page: number = 0,
    size: number = 10
  ): Observable<Page<Comment>> {
    return this.http.get<Page<Comment>>(`comments/user/${userId}`, {
      params: { page, size, sort: 'time,asc' },
    });
  }

  addComment(message: string, issueId: string): Observable<Comment> {
    return this.http.post<Comment>('comments', { message, issueId });
  }
}
