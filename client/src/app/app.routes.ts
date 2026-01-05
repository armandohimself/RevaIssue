// app.routes.ts is the map: when the URL is /projects, show the Projects page component.
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { AppShellComponent } from './features/layout/app-shell/app-shell.component';

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
        // later swap placeholder for AdminDashboardComponent
        component: LoginComponent,
      },
      {
        path: 'user/dashboard',
        // later swap placeholder for UserDashboardComponent
        component: LoginComponent,
      },
    ],
  },
  

  // catch-all
  { path: '**', redirectTo: 'login' },
];


