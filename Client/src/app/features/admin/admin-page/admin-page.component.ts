import {Component} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';

@Component({
  selector: 'app-admin-page.component',
  imports: [
    FilterByStatusPipe,
    LengthPipe
  ],
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.css'
})
export class AdminPageComponent {


  projects: ProjectDomain[] = [
  ];

  protected readonly ProjectStatusDomain = ProjectStatusDomain;
}
