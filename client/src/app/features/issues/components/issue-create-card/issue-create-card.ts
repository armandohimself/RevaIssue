import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IssueCreate, IssuePriority, IssueSeverity } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';

@Component({
  selector: 'app-issue-create-card',
  imports: [FormsModule],
  templateUrl: './issue-create-card.html',
  styleUrl: './issue-create-card.css',
})
export class IssueCreateCard {
  @Input({ required: true }) projectId!: string;
  @Output() closecard = new EventEmitter<void>();

  name = '';
  description = '';
  severity: IssueSeverity = 'LOW';
  priority: IssuePriority = 'LOW';

  constructor(private issueService: IssueService) {}

  onClose() {
    this.closecard.emit();
  }

  onCreate() {
    if (!this.name.trim()) {
      alert('Please enter an issue name');
      return;
    }

    const issueData: IssueCreate = {
      name: this.name,
      description: this.description,
      severity: this.severity,
      priority: this.priority
    };

    this.issueService.createIssue(this.projectId, issueData);
    this.closecard.emit();
  }
}
