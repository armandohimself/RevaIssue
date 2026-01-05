import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IssueData } from '../../interfaces/issue-data';

@Component({
  selector: 'app-issue-view-card',
  imports: [],
  templateUrl: './issue-view-card.html',
  styleUrl: './issue-view-card.css',
})
export class IssueViewCard {
    @Input({ required: true }) issue!: IssueData;
    @Output() closecard = new EventEmitter<void>();

  onClose() {
    this.closecard.emit();
  }
}
