import { Component } from '@angular/core';

@Component({
  selector: 'app-project-card',
  standalone: true, // Standalone means a component can declare its own dependencies directly, without needing a module.
  imports: [],
  templateUrl: './project-card.component.html',
  styleUrl: './project-card.component.css',
})
export class ProjectCardComponent {
  projectName: string = "I'm a project name";
  projectStatus: string = "ACTIVE";
}
