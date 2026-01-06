import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { IssueData, IssueStatus } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';
import { RoleService } from '../../services/role-service';

@Component({
  selector: 'app-issue-card',
  imports: [
    FormsModule, 
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule
  ],
  templateUrl: './issue-card.html',
  styleUrl: './issue-card.css',
})
export class IssueCard {
  @Input({ required: true }) issue!: IssueData;
  @Output() editclick = new EventEmitter<IssueData>();

  constructor(private issueService: IssueService, public roleService: RoleService) {}

  onEditClick(event: Event) {
    event.stopPropagation();
    this.editclick.emit(this.issue);
  }
  onStatusChange(event: Event) {
    event.stopPropagation();
    const newStatus = (event.target as HTMLSelectElement).value as IssueStatus;
    this.issueService.updateStatus(this.issue.issueId, newStatus);
  }
   get availableStatuses(): string[] {
    const roleStatuses = this.roleService.getAvailableStatuses();
    
    if (!roleStatuses.includes(this.issue.status)) {
      return [this.issue.status, ...roleStatuses];
    }
    
    return roleStatuses;
  }
}
