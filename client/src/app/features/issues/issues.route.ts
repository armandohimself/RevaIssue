import { Routes } from '@angular/router';
import { IssueList } from './components/issue-list/issue-list';

export const ISSUE_ROUTES: Routes = [
  { path: '', component: IssueList },
];