import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProjectCardComponent } from './features/projects/components/project-card/project-card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ProjectCardComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('client');
}
