import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';
import { NewProjectComponent } from '@features/customer/new-project/new-project.component';
import { useListProjectsMutation } from '@api/queries/project.query';
import { ProjectDetailModalComponent } from '@shared/project-detail-modal/project-detail-modal.component';

import {DatePipe, LowerCasePipe, NgClass, TitleCasePipe} from '@angular/common';
import {ProjectReviewComponent} from '@features/customer/project-review/project-review.component';
import {TranslatePipe} from '@ngx-translate/core';
import {LanguageService} from '@core/services/language.service';

@Component({
  selector: 'app-customer-page',
  standalone: true,
  imports: [
    MatDialogModule,
    DatePipe,
    NgClass,
    TranslatePipe,
    LowerCasePipe,

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

  constructor(public dialog: MatDialog, public languageService: LanguageService) {}

  ngOnInit(): void {
    this.fetchCustomerProjects().then();
  }

  async fetchCustomerProjects() {
    this.isLoading = true;
    try {
      this.projects = await this.getAllProjectsMutation.mutateAsync();
      if (this.projectsForCurrentView.length === 0) {
        this.autoSwitchView();
      }
    } catch (error) {
      console.error('Failed to fetch customer projects:', error);
    } finally {
      this.isLoading = false;
    }
  }

  get actionRequiredCount(): number {
    return this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED).length;
  }

  get projectsForCurrentView(): ProjectDomain[] {
    switch (this.currentView) {
      case 'action':
        return this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED);
      case 'progress':

        return this.projects.filter(p =>
          p.getStatusClass() === 'ASSIGNED' || p.getStatusClass() === 'REWORK'
        );
      case 'history':
        return this.projects.filter(p =>
          p.getStatusClass() === 'APPROVED' || p.getStatusClass() === 'CLOSED' || p.getStatusClass() === 'CANCELED'
        );
      default:
        return [];
    }
  }

  setView(view: 'action' | 'progress' | 'history'): void {
    this.currentView = view;
  }

  private autoSwitchView(): void {
    const actionCount = this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED).length;
    const progressCount = this.projects.filter(p =>
      p.getStatusClass() === 'ASSIGNED' || p.getStatusClass() === 'REWORK'
    ).length;

    if (actionCount > 0) {
      this.currentView = 'action';
    } else if (progressCount > 0) {
      this.currentView = 'progress';
    } else {
      this.currentView = 'history';
    }
  }

  openNewProjectModal(): void {
    const dialogRef = this.dialog.open(NewProjectComponent, {
      width: '600px',
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

  openProjectDetails(project: ProjectDomain): void {
    this.dialog.open(ProjectDetailModalComponent, {
      data: { project: project },
      width: '800px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });
  }

  openProjectReview(project: ProjectDomain): void {

    if (project.status !== ProjectStatusDomain.COMPLETED) {
      this.openProjectDetails(project);
      return;
    }
    this.dialog.open(ProjectReviewComponent, {
      data: { project: project },
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });
  }
}
