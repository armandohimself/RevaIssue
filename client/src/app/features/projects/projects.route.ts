import { Routes } from '@angular/router';
import { ProjectsListPage } from './pages/projects-list/projects-list.page';
import { ProjectsDetailPage } from './pages/projects-detail/projects-detail.page';

export const PROJECT_ROUTES: Routes = [
  { path: '', component: ProjectsListPage },
  { path: ':id', component: ProjectsDetailPage },
];
