import {Component} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';

@Component({
  selector: 'app-translator-page',
  imports: [
    FilterByStatusPipe,
    LengthPipe
  ],
  templateUrl: './translator-page.component.html',
  styleUrl: './translator-page.component.css'
})
export class TranslatorPageComponent {

  projects: ProjectDomain[] = [
  ];


  protected readonly ProjectStatusDomain = ProjectStatusDomain;
}
