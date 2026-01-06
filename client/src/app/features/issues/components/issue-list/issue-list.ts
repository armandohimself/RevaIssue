import { Component, signal, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ProjectsApi } from '../../../projects/data-access/projects.api';
import { ProjectResponse } from '../../../projects/models/project.model';
import { IssueData } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';
import { RoleService } from '../../services/role-service';
import { IssueCard } from "../issue-card/issue-card";
import { IssueCreateCard } from '../issue-create-card/issue-create-card';
import { IssueEditCard } from '../issue-edit-card/issue-edit-card';
import { IssueViewCard } from "../issue-view-card/issue-view-card";

@Component({
  selector: 'app-issue-list',
  imports: [
    IssueCard, 
    IssueViewCard, 
    FormsModule, 
    IssueCreateCard, 
    IssueEditCard,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCardModule,
    MatIconModule
  ],
  templateUrl: './issue-list.html',
  styleUrl: './issue-list.css',
})
export class IssueList {
  issues: WritableSignal<IssueData[]> = signal([]);
  selectedIssue: WritableSignal<IssueData | null> = signal(null);
  showCreateCard: WritableSignal<boolean> = signal(false);
  editingIssue: WritableSignal<IssueData | null> = signal(null);
  projects: WritableSignal<ProjectResponse[]> = signal([]);
  selectedProjectId = '';
  searchText = '';
  statusFilter = '';
  severityFilter = '';
  priorityFilter = '';
  showMyIssuesOnly = false;

  constructor(private issuesService: IssueService, private projectsApi: ProjectsApi, public roleService: RoleService) {
    this.issuesService.getIssuesSubject().subscribe(issueData => {
      this.issues.set(issueData);
    });
  }

  ngOnInit() {
    this.roleService.fetchUserRole();
    this.projectsApi.list().subscribe({
      next: (projects) => {
        this.projects.set(projects);
        if (projects.length > 0) {
          this.selectedProjectId = projects[0].projectId;
          this.loadIssues();
        }
      },
      error: (err) => console.error('Error loading projects:', err)
    });
  }
  onProjectChange() {
    if (this.selectedProjectId) {
      this.loadIssues();
    }
  }

  loadIssues() {
    if (this.showMyIssuesOnly) {
      const userId = this.roleService.getUserId();
      if (userId) {
        this.issuesService.getIssuesAssignedToUser(userId);
      } else {
        console.error('User ID not available');
        this.issuesService.getIssuesForProject(this.selectedProjectId);
      }
    } else {
      this.issuesService.getIssuesForProject(this.selectedProjectId);
    }
  }

  onMyIssuesToggle() {
    this.loadIssues();
  }

  getSelectedProjectName(): string {
    const project = this.projects().find(p => p.projectId === this.selectedProjectId);
    return project ? project.projectName : '';
  }
  
  getFilteredIssues(): IssueData[] {
      let result = this.issues();

      if (this.searchText) {
        const search = this.searchText.toLowerCase();
        result = result.filter(issue => issue.name.toLowerCase().includes(search));
      }

      if (this.statusFilter) {
        result = result.filter(issue => issue.status === this.statusFilter);
      }

      if (this.severityFilter) {
        result = result.filter(issue => issue.severity === this.severityFilter);
      }

      if (this.priorityFilter) {
        result = result.filter(issue => issue.priority === this.priorityFilter);
      }

      return result;
  }

  onViewIssue(issue: IssueData) {
    this.selectedIssue.set(issue);
  }
  onCloseViewCard() {
    this.selectedIssue.set(null);
  }

  onCreateIssue() {
    this.showCreateCard.set(true);
  }

  onCloseCreateCard() {
    this.showCreateCard.set(false);
  }

  onEditIssue(issue: IssueData) {
    this.editingIssue.set(issue);
  }

  onCloseEditCard() {
    this.editingIssue.set(null);
  }
}
