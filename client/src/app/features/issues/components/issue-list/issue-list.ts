import { Component, signal, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IssueData } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';
import { IssueCard } from "../issue-card/issue-card";
import { IssueCreateCard } from '../issue-create-card/issue-create-card';
import { IssueEditCard } from '../issue-edit-card/issue-edit-card';
import { IssueViewCard } from "../issue-view-card/issue-view-card";

@Component({
  selector: 'app-issue-list',
  imports: [IssueCard, IssueViewCard, FormsModule, IssueCreateCard, IssueEditCard],
  templateUrl: './issue-list.html',
  styleUrl: './issue-list.css',
})
export class IssueList {
  issues: WritableSignal<IssueData[]> = signal([]);
  selectedIssue: WritableSignal<IssueData | null> = signal(null);
  showCreateCard: WritableSignal<boolean> = signal(false);
  editingIssue: WritableSignal<IssueData | null> = signal(null);
  searchText = '';
  statusFilter = '';
  severityFilter = '';
  priorityFilter = '';

  testProjectId = '46f2de4b-c21b-4a1a-a08b-a1a81eaae516';

  constructor(private issuesService: IssueService) {
    this.issuesService.getIssuesSubject().subscribe(issueData => {
      this.issues.set(issueData);
    });
  }

  ngOnInit() {
    this.issuesService.getIssuesForProject(this.testProjectId);
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
