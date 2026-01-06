import { Component, EventEmitter, Input, OnInit, Output, signal, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../users/data-access/user-api';
import { UserService } from '../../../users/services/user';
import { IssueData, IssuePriority, IssueSeverity, IssueUpdate } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';


@Component({
  selector: 'app-issue-edit-card',
  imports: [FormsModule, MatButtonModule],
  templateUrl: './issue-edit-card.html',
  styleUrl: './issue-edit-card.css',
})
export class IssueEditCard implements OnInit {
  @Input({ required: true }) issue!: IssueData;
  @Output() closecard = new EventEmitter<void>();

  name = '';
  description = '';
  severity: IssueSeverity = 'LOW';
  priority: IssuePriority = 'LOW';
  submitted: WritableSignal<boolean> = signal(false);
  users: WritableSignal<User[]> = signal([]);
  selectedUserId: string | null = null;

  constructor(private issueService: IssueService, private userService: UserService) {}

  get developers(): User[] {
    return this.users().filter(user => user.role === 'DEVELOPER');
  }
  
  ngOnInit() {
    this.name = this.issue.name;
    this.description = this.issue.description;
    this.severity = this.issue.severity;
    this.priority = this.issue.priority;
    this.selectedUserId = this.issue.assignedToUserId || null;

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users.set(users);
      },
      error: (err: any) => console.error('Error loading users:', err)
    });
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
    
    if (this.selectedUserId && this.selectedUserId !== this.issue.assignedToUserId) {
      this.issueService.assignDeveloper(this.issue.issueId, this.selectedUserId);
    }

    this.closecard.emit();
  }
  onDelete() {
    if (confirm('Are you sure you want to delete this issue? This action cannot be undone.')) {
      this.issueService.deleteIssue(this.issue.issueId);
      this.closecard.emit();
    }
  }
}
