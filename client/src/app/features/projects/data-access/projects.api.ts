import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProjectsApi {
  private http = inject(HttpClient);
  private baseUrl = 'api/projects';

  list(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

}
