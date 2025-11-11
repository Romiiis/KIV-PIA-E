import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';
import { useListProjectsMutation } from '@api/queries/project.query';
import { ProjectDetailModalComponent } from '@shared/project-detail-modal/project-detail-modal.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import { LanguageListService } from '@core/services/languageList.service';
import { ToastrService } from 'ngx-toastr';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import {Observable, Subject, takeUntil} from 'rxjs';
import {MatIcon} from '@angular/material/icon';

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

  protected readonly ProjectStatusDomain = ProjectStatusDomain;
  protected readonly allStatuses = Object.values(ProjectStatusDomain);

  readonly listAllProjectsMutation = useListProjectsMutation();


  public statusLabel$!: Observable<string>;
  public feedbackLabel$!: Observable<string>;


  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    protected langList: LanguageListService,
    private translate: TranslateService,
  ) {
    this.statusLabel$ = this.translate.stream('adminPage.filter.status');
    this.feedbackLabel$ = this.translate.stream('adminPage.filter.feedback');
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
      this.applyFilters(); // Po načtení hned aplikujeme filtry
    } catch (error) {
      console.error('Failed to fetch all projects:', error);
      this.toastr.error('Failed to load projects.');
    } finally {
      this.isLoading = false;
    }
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let projects = [...this.allProjects];

    // 1. Filtr podle Stavu
    if (this.filterStatus !== 'all') {
      projects = projects.filter(p => p.status === this.filterStatus);
    }

    // 2. Filtr podle Feedbacku
    // Předpokládáme, že ProjectDomain má vlastnost `hasFeedback: boolean`
    if (this.filterFeedback === 'yes') {
      // projects = projects.filter(p => p.hasFeedback);
      // TODO: Upravit podle reálného modelu (pro účely dema filtrujeme ty, co mají feedback text)
      projects = projects.filter(p => p.feedback && p.feedback.text.length > 0);
    } else if (this.filterFeedback === 'no') {
      // projects = projects.filter(p => !p.hasFeedback);
      projects = projects.filter(p => !p.feedback || p.feedback.text.length === 0);
    }

    this.filteredProjects = projects;
  }

  openProjectManagement(project: ProjectDomain): void {
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

    // Nasloucháme, co se stane po zavření
    dialogRef.afterClosed().subscribe(result => {
      if (result === 'projectUpdated') {
        this.fetchAllProjects();
      }
    });
  }
}
