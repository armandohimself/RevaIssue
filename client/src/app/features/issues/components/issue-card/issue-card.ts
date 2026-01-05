import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IssueData } from '../../interfaces/issue-data';

@Component({
  selector: 'app-issue-card',
  imports: [],
  templateUrl: './issue-card.html',
  styleUrl: './issue-card.css',
})
export class IssueCard {
  @Input({ required: true }) issue!: IssueData;
  @Output() editclick = new EventEmitter<IssueData>();

  onEditClick(event: Event) {
    event.stopPropagation();
    this.editclick.emit(this.issue);
  }
}
