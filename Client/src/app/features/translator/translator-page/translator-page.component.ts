import {Component, OnInit} from '@angular/core';

import {CommonModule, DatePipe, TitleCasePipe} from '@angular/common';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {MatIconModule} from '@angular/material/icon';
import {ToastrService} from 'ngx-toastr';
import {ProjectDetailModalComponent} from '@shared/project-detail-modal/project-detail-modal.component';
import {useListProjectsMutation} from '@api/queries/project.query';
import {
  ProjectSubmissionComponent
} from '@features/translator/project-submission.component/project-submission.component';
import {TranslatePipe} from '@ngx-translate/core';
import {LanguageListService} from '@core/services/languageList.service';

@Component({
  selector: 'app-translator-page',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    DatePipe,
    MatIconModule,
    TranslatePipe
  ],
  templateUrl: './translator-page.component.html',
  styleUrls: ['./translator-page.component.css']
})
export class TranslatorPageComponent implements OnInit {

  projects: ProjectDomain[] = [];
  isLoading = false;
  readonly listTranslatorProjectsMutation = useListProjectsMutation();
  currentView: 'action' | 'waiting' | 'history' = 'action';
  protected readonly ProjectStatusDomain = ProjectStatusDomain;

  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    protected langList: LanguageListService
  ) {
  }

  get actionRequiredCount(): number {
    return this.projects.filter(p => {
      const statusClass = p.getStatusClass();
      return statusClass === 'ASSIGNED' || statusClass === 'REWORK';
    }).length;
  }

  get projectsForCurrentView(): ProjectDomain[] {
    switch (this.currentView) {
      case 'action':
        return this.projects.filter(p => {
          const statusClass = p.getStatusClass();
          return statusClass === 'ASSIGNED' || statusClass === 'REWORK';
        });

      case 'waiting':
        return this.projects.filter(p => p.getStatusClass() === 'COMPLETED');

      case 'history':
        return this.projects.filter(p => {
          const statusClass = p.getStatusClass();
          return statusClass === 'APPROVED' || statusClass === 'CLOSED' || statusClass === 'CANCELED';
        });

      default:
        return [];
    }
  }

  ngOnInit(): void {
    this.fetchTranslatorProjects().then();
  }

  async fetchTranslatorProjects() {
    this.isLoading = true;
    try {
      this.projects = await this.listTranslatorProjectsMutation.mutateAsync();
      console.log('Fetched translator projects:', this.projects);

      if (this.projectsForCurrentView.length === 0) {
        this.autoSwitchView();
      }
    } catch (error) {
      console.error('Failed to fetch translator projects:', error);
      this.toastr.error('Failed to load projects.');
    } finally {
      this.isLoading = false;
    }
  }

  setView(view: 'action' | 'waiting' | 'history'): void {
    this.currentView = view;
  }

  openProjectSubmission(project: ProjectDomain): void {
    const dialogRef = this.dialog.open(ProjectSubmissionComponent, {
      data: {project: project},
      width: '600px',
      maxWidth: '95vw',
      maxHeight: '100vh',
      panelClass: 'clean-dialog-panel',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'submitted') {
        this.fetchTranslatorProjects();
      }
    });
  }

  openProjectDetails(project: ProjectDomain): void {
    this.dialog.open(ProjectDetailModalComponent, {
      data: {project: project},
      width: '800px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });
  }

  private autoSwitchView(): void {
    if (this.actionRequiredCount > 0) {
      this.currentView = 'action';
    } else if (this.projects.filter(p => p.getStatusClass() === 'COMPLETED').length > 0) {
      this.currentView = 'waiting';
    } else {
      this.currentView = 'history';
    }
  }


}
