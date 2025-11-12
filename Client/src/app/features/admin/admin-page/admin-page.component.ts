import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {useListProjectsMutation} from '@api/queries/project.query'; // Předpokládáme, že zde existuje i mutace pro uzavření
import {ProjectDetailModalComponent} from '@shared/project-detail-modal/project-detail-modal.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {LanguageListService} from '@core/services/languageList.service';
import {ToastrService} from 'ngx-toastr';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatOptionModule} from '@angular/material/core';
import {FormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {MatIcon} from '@angular/material/icon';
import {AdminActionModalComponent} from '@features/admin/admin-action-modal.component/admin-action-modal.component';
import {useCloseProjectMutation} from '@api/queries/workflow.query';


@Component({
  selector: 'app-admin-page',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    DatePipe,
    TranslatePipe,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    FormsModule,
    MatIcon
  ],
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.css']
})
export class AdminPageComponent implements OnInit {

  private allProjects: ProjectDomain[] = [];
  public filteredProjects: ProjectDomain[] = [];
  public isLoading = false;

  public filterStatus: string = 'all';
  public filterFeedback: 'all' | 'yes' | 'no' = 'all';
  public filterCustomer: string = 'all';
  public filterTranslator: string = 'all';

  public customerList: { id: string, name: string }[] = [];
  public translatorList: { id: string, name: string }[] = [];

  protected readonly allStatuses = Object.values(ProjectStatusDomain);

  readonly listAllProjectsMutation = useListProjectsMutation();
  readonly closeProjectMutation = useCloseProjectMutation();


  public statusLabel$!: Observable<string>;
  public feedbackLabel$!: Observable<string>;
  public customerLabel$!: Observable<string>;
  public translatorLabel$!: Observable<string>;


  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    protected langList: LanguageListService,
    private translate: TranslateService,
  ) {
    this.statusLabel$ = this.translate.stream('adminPage.filter.status');
    this.feedbackLabel$ = this.translate.stream('adminPage.filter.feedback');
    this.customerLabel$ = this.translate.stream('adminPage.filter.customer');
    this.translatorLabel$ = this.translate.stream('adminPage.filter.translator');
  }

  public areLabelsReady = false;

  ngOnInit(): void {
    this.fetchAllProjects().then();
    this.translate.get('adminPage.filter.status').subscribe({
      next: () => {
        this.areLabelsReady = true;
      },
      error: () => {
        this.areLabelsReady = true;
      }
    });
  }

  async fetchAllProjects() {
    this.isLoading = true;
    try {
      this.allProjects = await this.listAllProjectsMutation.mutateAsync();
      this.populateUserFilters();
      this.applyFilters();
    } catch (error) {
      console.error('Failed to fetch all projects:', error);
      let errorMessage = this.translate.instant('adminPage.toastLoadError');
      this.toastr.error(errorMessage);
    } finally {
      this.isLoading = false;
    }
  }

  private populateUserFilters(): void {
    const customerMap = new Map<string, string>();
    const translatorMap = new Map<string, string>();

    for (const project of this.allProjects) {
      if (project.customer) {
        customerMap.set(project.customer.id, project.customer.name);
      }
      if (project.translator) {
        translatorMap.set(project.translator.id, project.translator.name);
      }
    }

    this.customerList = Array.from(customerMap.entries())
      .map(([id, name]) => ({id, name}))
      .sort((a, b) => a.name.localeCompare(b.name));

    this.translatorList = Array.from(translatorMap.entries())
      .map(([id, name]) => ({id, name}))
      .sort((a, b) => a.name.localeCompare(b.name));
  }


  onFilterChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let projects = [...this.allProjects];

    if (this.filterStatus !== 'all') {
      projects = projects.filter(p => p.status === this.filterStatus);
    }

    if (this.filterFeedback === 'yes') {
      projects = projects.filter(p => p.feedback && p.feedback.text.length > 0);
    } else if (this.filterFeedback === 'no') {
      projects = projects.filter(p => !p.feedback || p.feedback.text.length === 0);
    }

    if (this.filterCustomer !== 'all') {
      projects = projects.filter(p => p.customer.id === this.filterCustomer);
    }
    if (this.filterTranslator !== 'all') {
      projects = projects.filter(p => p.translator?.id === this.filterTranslator);
    }

    this.filteredProjects = projects;
  }

  openProjectDetails(project: ProjectDomain): void {
    const dialogRef = this.dialog.open(ProjectDetailModalComponent, {
      data: {
        project: project,
        isAdminView: true
      },
      width: '800px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'projectUpdated') {
        this.fetchAllProjects();
      }
    });
  }

  openAdminActions(project: ProjectDomain): void {
    const dialogRef = this.dialog.open(AdminActionModalComponent, {
      data: {project: project},
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'projectUpdated') {
        this.fetchAllProjects();
      }
    });
  }

  async onCloseProject(project: ProjectDomain): Promise<void> {
    const confirmClose = confirm(
      this.translate.instant('adminPage.confirmCloseMessage', {projectName: project.originalFileName})
    );

    if (!confirmClose) {
      return;
    }
    try {
      await this.closeProjectMutation.mutateAsync(project.id)

      let successMsg = this.translate.instant("adminPage.toastCloseSuccess");
      this.toastr.success(successMsg);

      await this.fetchAllProjects();

    } catch (error) {
      let errorMsg = this.translate.instant("adminPage.toastCloseError");
      this.toastr.error(errorMsg);
    } finally {
      this.isLoading = false;
    }
  }


  private highlightProject(projectId: string): void {
    setTimeout(() => {
      const elementId = `project-card-${projectId}`;
      const element = document.getElementById(elementId);

      if (element) {
        element.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
          inline: 'nearest'
        });

        element.classList.add('highlighted-card');

        setTimeout(() => {
          element.classList.remove('highlighted-card');
        }, 2500);
      }
    }, 0);
  }

}
