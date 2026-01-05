import { Component, signal, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IssueData } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';
import { IssueCard } from "../issue-card/issue-card";
import { IssueViewCard } from "../issue-view-card/issue-view-card";

@Component({
  selector: 'app-issue-list',
  imports: [IssueCard, IssueViewCard, FormsModule],
  templateUrl: './issue-list.html',
  styleUrl: './issue-list.css',
})
export class IssueList {
  issues: WritableSignal<IssueData[]> = signal([]);
  selectedIssue: WritableSignal<IssueData | null> = signal(null);
  searchText = '';
  statusFilter = '';

  testProjectId = 'b26d094d-c384-4b47-9fa2-1c984053ef92';

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

      return result;
  }

  onViewIssue(issue: IssueData) {
    this.selectedIssue.set(issue);
  }
  onCloseViewCard() {
    this.selectedIssue.set(null);
  }

  onEditIssue(issue: IssueData) {
    console.log('Edit issue:', issue);
  }
}
