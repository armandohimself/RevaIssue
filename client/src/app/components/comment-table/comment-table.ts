import {
  Component,
  ElementRef,
  Input,
  OnInit,
  signal,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { CommentService } from '../../services/comment-service';
import { Comment } from '../../interfaces/comment';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { Page } from '../../interfaces/page';

@Component({
  selector: 'app-comment-table',
  imports: [FormsModule, DatePipe, CommonModule],
  templateUrl: './comment-table.html',
  styleUrl: './comment-table.css',
})
export class CommentTable implements OnInit {
  @Input() issueId!: string;
  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  comments: WritableSignal<Comment[]> = signal([]);
  newMessage: string = '';
  page: number = 0;
  loading: boolean = false;
  hasMore: boolean = true;

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {
    this.loadComments();
  }

  loadComments(): void {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.commentService.getCommentsByIssueId(this.issueId, this.page).subscribe({
      next: (pageData: Page<Comment>) => {
        this.comments.set([...this.comments(), ...pageData.content]);
        this.hasMore = !pageData.last;
        this.page++;
        this.loading = false;
        const container = this.scrollContainer.nativeElement;
        if (container.scrollHeight <= container.clientHeight && this.hasMore) {
          this.loadComments();
        }
      },
      error: (error) => {
        console.error('Error loading comments:', error);
        this.loading = false;
      },
    });
  }

  onScroll(): void {
    const element = this.scrollContainer.nativeElement;
    if (element.scrollTop + element.clientHeight >= element.scrollHeight - 100) {
      this.loadComments();
    }
  }

  submitComment(): void {
    if (!this.newMessage.trim()) return;
    this.commentService.addComment(this.newMessage, this.issueId).subscribe((newComment) => {
      this.comments.set([...this.comments(), newComment]);
      this.newMessage = '';
      setTimeout(() => {
        this.scrollContainer.nativeElement.scrollTop =
          this.scrollContainer.nativeElement.scrollHeight;
      }, 0);
    });
  }
}
