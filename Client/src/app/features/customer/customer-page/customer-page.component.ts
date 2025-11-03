import {Component} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {NewProjectComponent} from '@features/customer/new-project/new-project.component';
import {NgIf} from '@angular/common';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';

@Component({
  selector: 'app-customer-page',
  templateUrl: './customer-page.component.html',
  imports: [
    FilterByStatusPipe,
    LengthPipe,
    NewProjectComponent,
    NgIf
  ],
  styleUrls: ['./customer-page.component.css']
})
export class CustomerPageComponent {

  showNewProject!: boolean

  openNewProject() {
    console.log('clicked!');
    this.showNewProject = true;
  }

  closeNewProject() {
    this.showNewProject = false;
  }


  projects: ProjectDomain[] = [];


  protected readonly ProjectStatusDomain = ProjectStatusDomain;
}
