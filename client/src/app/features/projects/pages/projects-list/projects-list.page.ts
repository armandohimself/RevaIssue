import { Component } from '@angular/core';
import { ProjectCardComponent } from '../../components/project-card/project-card.component';

@Component({
  selector: 'app-projects-list.page',
  standalone: true,
  imports: [ProjectCardComponent],
  templateUrl: './projects-list.page.html',
  styleUrl: './projects-list.page.css',
})
export class ProjectsListPage {

}
