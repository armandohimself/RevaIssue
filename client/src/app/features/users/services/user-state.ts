import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UserStateService {
  private refreshTrigger = signal(0);
  // priv 
  readonly refreshNeeded = this.refreshTrigger.asReadonly();

  triggerRefresh(): void {
    this.refreshTrigger.update(value => value + 1);
  }
}
