import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GrantProjectAccessRequest, ProjectAccessResponse } from '../models/project-access.model';

@Injectable({ providedIn: 'root' })
export class ProjectAccessApi {
  private http = inject(HttpClient);

  list(projectId: string): Observable<ProjectAccessResponse[]> {
    return this.http.get<ProjectAccessResponse[]>(`api/projects/${projectId}/access`);
  }

  grant(projectId: string, body: GrantProjectAccessRequest): Observable<ProjectAccessResponse> {
    return this.http.post<ProjectAccessResponse>(`api/projects/${projectId}/access`, body);
  }

  revoke(projectId: string, userId: string): Observable<void> {
    return this.http.delete<void>(`api/projects/${projectId}/access/${userId}`);
  }
}
