import {Component, OnInit} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {NewProjectComponent} from '@features/customer/new-project/new-project.component';
import {NgIf} from '@angular/common';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {useListProjectsMutation} from '@api/queries/project.query';

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

/**
 * Customer page component that displays customer projects
 * and allows creating new projects.
 */
export class CustomerPageComponent implements OnInit {

  // Boolean to control the visibility of the new project modal dialog
  showNewProject!: boolean;

  // Exposes project status domain to the template
  protected readonly ProjectStatusDomain = ProjectStatusDomain;

  // List of projects for the customer
  projects: ProjectDomain[] = [];

  readonly getAllProjectsMutation = useListProjectsMutation();



  ngOnInit(): void {
    this.fetchCustomerProjects().then();
  }

  /**
   * Fetches projects asynchronously and updates the component's state.
   */
  async fetchCustomerProjects() {
    try {
      this.projects = await this.getAllProjectsMutation.mutateAsync();
    } catch (error) {
      console.error('Failed to fetch customer projects:', error);
    }
  }

  /**
   * Opens the new project modal dialog.
   */
  openNewProject() {
    this.showNewProject = true;
  }

  /**
   * Closes the new project modal dialog.
   */
  closeNewProject() {
    this.showNewProject = false;
  }
}
