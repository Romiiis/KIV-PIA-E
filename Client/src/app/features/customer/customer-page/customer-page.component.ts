import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';
import { NewProjectComponent } from '@features/customer/new-project/new-project.component';
import { useListProjectsMutation } from '@api/queries/project.query';
import { ProjectDetailModalComponent } from '@shared/project-detail-modal/project-detail-modal.component';
import { DatePipe } from '@angular/common';
import {ProjectReviewComponent} from '@features/customer/project-review/project-review.component'; // We still need DatePipe

@Component({
  selector: 'app-customer-page',
  standalone: true,
  imports: [
    MatDialogModule,
    DatePipe // Keep DatePipe for formatting
  ],
  templateUrl: './customer-page.component.html',
  styleUrls: ['./customer-page.component.css']
})
export class CustomerPageComponent implements OnInit {
  protected readonly ProjectStatusDomain = ProjectStatusDomain;
  projects: ProjectDomain[] = [];
  isLoading = false;
  readonly getAllProjectsMutation = useListProjectsMutation();

  currentView: 'action' | 'progress' | 'history' = 'action';

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    this.fetchCustomerProjects().then();
  }

  async fetchCustomerProjects() {
    this.isLoading = true;
    try {
      this.projects = await this.getAllProjectsMutation.mutateAsync();
      // Auto-switch view logic (unchanged, but now uses .status)
      if (this.projectsForCurrentView.length === 0) {
        this.autoSwitchView();
      }
    } catch (error) {
      console.error('Failed to fetch customer projects:', error);
    } finally {
      this.isLoading = false;
    }
  }

  /**
   * Returns the count of projects requiring customer action (COMPLETED state).
   */
  get actionRequiredCount(): number {
    return this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED).length;
  }

  /**
   * IMPORTANT FIX: Now uses 'p.status' as defined by the ProjectDomain model
   * '@Expose({name: 'state'}) status!: ProjectStatusDomain'
   */
  get projectsForCurrentView(): ProjectDomain[] {
    switch (this.currentView) {
      case 'action':
        return this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED);
      case 'progress':
        return this.projects.filter(p => p.status === ProjectStatusDomain.ASSIGNED);
      case 'history':
        return this.projects.filter(p => p.status === ProjectStatusDomain.APPROVED || p.status === ProjectStatusDomain.CLOSED);
      default:
        return [];
    }
  }

  setView(view: 'action' | 'progress' | 'history'): void {
    this.currentView = view;
  }

  /**
   * Auto-switch logic (unchanged, but now uses .status)
   */
  private autoSwitchView(): void {
    const actionCount = this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED).length;
    const progressCount = this.projects.filter(p => p.status === ProjectStatusDomain.ASSIGNED).length;

    if (actionCount > 0) {
      this.currentView = 'action';
    } else if (progressCount > 0) {
      this.currentView = 'progress';
    } else {
      this.currentView = 'history';
    }
  }

  /**
   * Opens the new project modal (unchanged).
   */
  openNewProjectModal(): void {
    const dialogRef = this.dialog.open(NewProjectComponent, {
      minWidth: '500px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'created') {
        this.fetchCustomerProjects();
      }
    });
  }

  /**
   * Opens the project details modal (unchanged).
   */
  openProjectDetails(project: ProjectDomain): void {
    this.dialog.open(ProjectDetailModalComponent, {
      data: { project: project },
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });
  }

  openProjectReview(project: ProjectDomain): void {
    this.dialog.open(ProjectReviewComponent, {
      data: { project: project },
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });
  }
}
