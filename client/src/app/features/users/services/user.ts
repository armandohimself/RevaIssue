import { Injectable } from '@angular/core';
import { UserApiService } from '../data-access/user-api';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private userApi: UserApiService) {}

  getAllUsers() {
    return this.userApi.getAllUsers();
  }

  createUser(request: any) {
    return this.userApi.createUser(request);
  }

  deleteUser(userId: string) {
    return this.userApi.deleteUser(userId);
  }
}
