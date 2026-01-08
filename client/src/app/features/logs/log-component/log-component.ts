import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { LogService } from '../service/log-service';
import { LogTransaction } from '../data-access/log-api-service';

@Component({
  selector: 'app-logs',
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatPaginatorModule,
    MatChipsModule
  ],
  templateUrl: './log-component.html',
  styleUrl: './log-component.css'
})
export class LogsComponent implements OnInit {
  logs = signal<LogTransaction[]>([]);
  isLoading = signal(true);
  totalElements = signal(0);
  pageSize = signal(20);
  pageIndex = signal(0);
  displayedColumns = ['date', 'user', 'entityType', 'message'];

  constructor(private logService: LogService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.isLoading.set(true);
    this.logService.getAllLogs(this.pageIndex(), this.pageSize()).subscribe({
      next: (response) => {
        this.logs.set(response.content);
        this.totalElements.set(response.totalElements);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load logs:', error);
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadLogs();
  }

  getEntityTypeColor(entityType: string): string {
    switch (entityType) {
      case 'ISSUE': return 'primary';
      case 'PROJECT': return 'accent';
      case 'USER': return 'warn';
      default: return '';
    }
  }
}
