import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; // <-- Import MatDialog
import { FilterByStatusPipe } from '@shared/pipes/filter-by-status-pipe';
import { LengthPipe } from '@shared/pipes/length-pipe';
import { NewProjectComponent } from '@features/customer/new-project/new-project.component';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';
import { useListProjectsMutation } from '@api/queries/project.query';
import { ProjectDetailModalComponent } from '@shared/project-detail-modal/project-detail-modal.component';

@Component({
  selector: 'app-customer-page',
  standalone: true,
  imports: [
    FilterByStatusPipe,
    LengthPipe,
    MatDialogModule
  ],
  templateUrl: './customer-page.component.html',
  styleUrls: ['./customer-page.component.css']
})
export class CustomerPageComponent implements OnInit {
  protected readonly ProjectStatusDomain = ProjectStatusDomain;
  projects: ProjectDomain[] = [];
  isLoading = false;
  readonly getAllProjectsMutation = useListProjectsMutation();

  // Vložíme si službu MatDialog
  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    this.fetchCustomerProjects().then();
  }

  async fetchCustomerProjects() {
    this.isLoading = true;
    try {
      this.projects = await this.getAllProjectsMutation.mutateAsync();
    } catch (error) {
      console.error('Failed to fetch customer projects:', error);
    } finally {
      this.isLoading = false;
    }
  }

  /**
   * Opens the new project modal dialog using Angular Material.
   */
  openNewProjectModal(): void {
    const dialogRef = this.dialog.open(NewProjectComponent, {
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel', // <-- POUŽÍVÁME NOVOU TŘÍDU
      disableClose: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'created') {
        this.fetchCustomerProjects();
      }
    });
  }

  /**
   * Opens the project details modal dialog using Angular Material.
   * @param project The project to display.
   */
  openProjectDetails(project: ProjectDomain): void {
    this.dialog.open(ProjectDetailModalComponent, {
      data: { project: project },
      width: '650px',       // Pevná šířka pro desktopy
      maxWidth: '95vw',     // Zajistí, že na mobilu nebude širší než obrazovka
      maxHeight: '90vh',    // Maximální výška je 90% výšky obrazovky
    });
  }
}
