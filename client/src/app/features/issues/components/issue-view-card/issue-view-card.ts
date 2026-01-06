import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, signal, WritableSignal } from '@angular/core';
import { CommentTable } from '../../../../components/comment-table/comment-table';
import { Page } from '../../../../interfaces/page';
import { IssueData, LogTransaction } from '../../interfaces/issue-data';
import { IssueService } from '../../services/issue-service';

@Component({
  selector: 'app-issue-view-card',
  imports: [CommonModule, CommentTable],
  templateUrl: './issue-view-card.html',
  styleUrl: './issue-view-card.css',
})
export class IssueViewCard implements OnInit{
  @Input({ required: true }) issue!: IssueData;
  @Output() closecard = new EventEmitter<void>();
  logs: WritableSignal<LogTransaction[]> = signal([]);

  constructor(private issueService: IssueService){}

  ngOnInit(): void {
      this.loadLogs();
  }

  onClose() {
    this.closecard.emit();
  }
  loadLogs() {
    this.issueService.getIssueHistory(this.issue.issueId).subscribe({
      next: (page: Page<LogTransaction>) => {
        this.logs.set(page.content);
      },
      error: (err: any) => {
        console.error('Error loading logs:', err);
      }
    });
  }
}
