// app.routes.ts is the map: when the URL is /projects, show the Projects page component.
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'projects',
    loadChildren: () =>
        import('./features/projects/projects.route').then(m => m.PROJECT_ROUTES),
  },
];
