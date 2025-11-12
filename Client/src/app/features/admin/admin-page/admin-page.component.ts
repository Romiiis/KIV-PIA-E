import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';
import { useListProjectsMutation } from '@api/queries/project.query'; // Předpokládáme, že zde existuje i mutace pro uzavření
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
import {AdminActionModalComponent} from '@features/admin/admin-action-modal.component/admin-action-modal.component';


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
  public filterCustomer: string = 'all'; // Přidáno zpět
  public filterTranslator: string = 'all'; // Přidáno zpět

  // Přidáno zpět pro filtry
  public customerList: { id: string, name: string }[] = [];
  public translatorList: { id: string, name: string }[] = [];

  protected readonly ProjectStatusDomain = ProjectStatusDomain;
  protected readonly allStatuses = Object.values(ProjectStatusDomain);

  readonly listAllProjectsMutation = useListProjectsMutation();
  // readonly closeProjectMutation = useCloseProjectMutation(); // Předpokládáme


  public statusLabel$!: Observable<string>;
  public feedbackLabel$!: Observable<string>;
  public customerLabel$!: Observable<string>; // Přidáno zpět
  public translatorLabel$!: Observable<string>; // Přidáno zpět


  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    protected langList: LanguageListService,
    private translate: TranslateService,
  ) {
    this.statusLabel$ = this.translate.stream('adminPage.filter.status');
    this.feedbackLabel$ = this.translate.stream('adminPage.filter.feedback');
    this.customerLabel$ = this.translate.stream('adminPage.filter.customer'); // Přidáno zpět
    this.translatorLabel$ = this.translate.stream('adminPage.filter.translator'); // Přidáno zpět
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
      this.populateUserFilters(); // Přidáno zpět
      this.applyFilters();
    } catch (error) {
      console.error('Failed to fetch all projects:', error);
      this.toastr.error('Failed to load projects.');
    } finally {
      this.isLoading = false;
    }
  }

  // Přidáno zpět
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
      .map(([id, name]) => ({ id, name }))
      .sort((a, b) => a.name.localeCompare(b.name));

    this.translatorList = Array.from(translatorMap.entries())
      .map(([id, name]) => ({ id, name }))
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

    // Přidáno zpět
    if (this.filterCustomer !== 'all') {
      projects = projects.filter(p => p.customer.id === this.filterCustomer);
    }
    if (this.filterTranslator !== 'all') {
      projects = projects.filter(p => p.translator?.id === this.filterTranslator);
    }

    this.filteredProjects = projects;
  }

  // Otevře modál s detailem projektu (read-only)
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

  // Otevře modál POUZE pro odeslání zprávy
  openAdminActions(project: ProjectDomain): void {
    const dialogRef = this.dialog.open(AdminActionModalComponent, {
      data: { project: project },
      width: '600px', // Může být menší, už tam nejsou taby
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: false
    });

    dialogRef.afterClosed().subscribe(result => {
      // Modál pro zprávy teď může taky vrátit 'projectUpdated' (ale nemusí)
      if (result === 'projectUpdated') {
        this.fetchAllProjects();
      }
    });
  }

  // --- NOVÁ METODA PRO TLAČÍTKO UZAVŘÍT ---
  async onCloseProject(project: ProjectDomain): Promise<void> {
    // Jednoduchá vestavěná konfirmace
    const confirmClose = confirm(
      this.translate.instant('adminPage.confirmCloseMessage', { projectName: project.originalFileName })
    );

    if (!confirmClose) {
      return;
    }

    // Zde by normálně byl spinner, ale na kartě je to složitější
    // Takže rovnou simulujeme volání
    try {
      // await this.closeProjectMutation.mutateAsync(project.id);
      await new Promise(resolve => setTimeout(resolve, 750)); // Simulace

      this.toastr.success(
        this.translate.instant('adminActionModal.toastCloseSuccess')
      );
      this.fetchAllProjects(); // Obnovení seznamu

    } catch (error) {
      this.toastr.error(
        this.translate.instant('adminActionModal.toastCloseError')
      );
      console.error("Failed to close project", error);
    }
  }
}
