// app.routes.ts is the map: when the URL is /projects, show the Projects page component.
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';

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
  },
  {
    path: 'admin/dashboard',
    component: LoginComponent // placeholder for now / admin dashboard
  },
  {
    path: 'user/dashboard',
    component: LoginComponent // placeholder for now - tester / dev
  },
  {
    path: 'issues',
    loadChildren: () =>
        import('./features/issues/issues.route').then(m => m.ISSUE_ROUTES)
  }
];


