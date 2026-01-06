// app.routes.ts is the map: when the URL is /projects, show the Projects page component.
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { authGuard } from './guards/auth/auth-guard';
import { adminGuard } from './guards/admin/admin-guard';
import { AddUserComponent } from './features/users/add-user/add-user';
import { ListUsersComponent } from './features/users/list-users/list-users';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/login'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'projects',
    loadChildren: () =>
        import('./features/projects/projects.route').then(m => m.PROJECT_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'admin/dashboard',
    component: AddUserComponent, // placeholder for now / admin dashboard
    canActivate: [authGuard, adminGuard] // if auth is good, goes to check if user is admin
  },
  {
    path: 'user/dashboard',
    component: LoginComponent, // placeholder for now - tester / dev
    canActivate: [authGuard] // may need additional guards for tester / developer
  },
  {
    path: 'admin/users/add',
    component: AddUserComponent,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'admin/users/get-all',
    component: ListUsersComponent,
    canActivate: [authGuard, adminGuard]
  },
];
