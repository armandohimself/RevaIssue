import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatIconModule  } from "@angular/material/icon";

@Component({
  selector: 'app-logout',
  imports: [MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>logout</mat-icon>
      Confirm Logout
    </h2>
    <mat-dialog-content>
      <p>Are you sure you want to log out?</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button id="cancel-logout" mat-button [mat-dialog-close]="false">Cancel</button>
      <button id="confirm-logout" mat-raised-button color="primary" [mat-dialog-close]="true">Logout</button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    mat-dialog-actions {
      gap: 0.5rem;
    }
  `]
})
export class LogoutComponent {
  constructor(public dialogRef: MatDialogRef<LogoutComponent>) {}
}
