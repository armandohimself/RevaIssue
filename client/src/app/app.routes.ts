// app.routes.ts is the map: when the URL is /projects, show the Projects page component.
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { authGuard } from './guards/auth/auth-guard';
import { adminGuard } from './guards/admin/admin-guard';
import { AddUserComponent } from './features/users/add-user/add-user';
import { ListUsersComponent } from './features/users/list-users/list-users';
import { AppShellComponent } from './features/layout/app-shell/app-shell.component';
import { AdminDashboardComponent } from './features/admin/dashboard/dashboard';
import { ProjectsPage } from './features/projects/pages/projects-page/projects-page';

export const routes: Routes = [
  // default landing
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  // public route (no shell)
  {
    path: 'login',
    component: LoginComponent,
  },

  // protected/app routes (with our shell)
  {
    path: '',
    component: AppShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'projects',
        loadChildren: () =>
          import('./features/projects/projects.route').then((m) => m.PROJECT_ROUTES),
      },
      {
        path: 'issues',
        loadChildren: () =>
            import('./features/issues/issues.route').then(m => m.ISSUE_ROUTES)
      },
      {
        path: 'admin/dashboard',
        component: AdminDashboardComponent,
        canActivate: [adminGuard]
      },
      {
        path: 'user/dashboard',
        // later swap placeholder for UserDashboardComponent
        component: ProjectsPage
      },
      {
        path: 'admin/users/add',
        component: AddUserComponent,
        canActivate: [adminGuard]
      },
      {
        path: 'admin/users/get-all',
        component: ListUsersComponent,
        canActivate: [adminGuard]
      },
    ],
  },


  // catch-all
  { path: '**', redirectTo: 'login' },
];


