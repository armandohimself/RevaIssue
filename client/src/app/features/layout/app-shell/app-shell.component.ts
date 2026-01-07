import { Component, inject, signal } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../auth/services/auth';
import { LogoutComponent } from '../../auth/logout/logout';
import { AuthApiService } from '../../auth/data-access/auth-api';

@Component({
  selector: 'app-app-shell',
  standalone: true,
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatDialogModule,
    AsyncPipe,
    RouterOutlet,
    RouterLink,
  ]
})
export class AppShellComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);
  private authApi = inject(AuthApiService);

  isAdmin = signal(false);

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
  );

  ngOnInit(): void {
    this.checkUserRole();
  }

  checkUserRole(): void {
    this.authApi.getCurrentUser().subscribe({
      next: (user) => {
        this.isAdmin.set(user.role === 'ADMIN');
      },
      error: (error) => {
        console.error('Failed to get user info:', error);
        this.isAdmin.set(false);
      }
    });
  }

  onLogout(): void {
    const dialogRef = this.dialog.open(LogoutComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.authService.logout();
      }
    });
  }
}
