import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IssueData, IssuePriority, IssueSeverity, IssueUpdate } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';

@Component({
  selector: 'app-issue-edit-card',
  imports: [FormsModule],
  templateUrl: './issue-edit-card.html',
  styleUrl: './issue-edit-card.css',
})
export class IssueEditCard {
  @Input({ required: true }) issue!: IssueData;
  @Output() closecard = new EventEmitter<void>();

  name = '';
  description = '';
  severity: IssueSeverity = 'LOW';
  priority: IssuePriority = 'LOW';

  constructor(private issueService: IssueService) {}

  ngOnInit() {
    this.name = this.issue.name;
    this.description = this.issue.description;
    this.severity = this.issue.severity;
    this.priority = this.issue.priority;
  }

  onClose() {
    this.closecard.emit();
  }

  onUpdate() {
    if (!this.name.trim()) {
      alert('Please enter an issue name');
      return;
    }

    const issueData: IssueUpdate = {
      name: this.name,
      description: this.description,
      severity: this.severity,
      priority: this.priority
    };

    this.issueService.updateIssue(this.issue.issueId, issueData);
    this.closecard.emit();
  }
}
