import {Component, Inject} from '@angular/core';
import {CommonModule, TitleCasePipe} from '@angular/common';
import {ProjectDomain} from '@core/models/project.model';
// MatDialog jsme přidali, MatDialogModule už tu byl
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ToastrService} from 'ngx-toastr';
import {LanguageListService} from '@core/services/languageList.service';
import {useDownloadOriginalMutation, useDownloadTranslatedMutation,} from '@api/queries/project.query';
import {ProjectModalLayoutComponent} from '@shared/project-modal-layout/project-modal-layout.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {FormsModule} from '@angular/forms';
import {MatFormField, MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {AdminActionModalComponent} from '@features/admin/admin-action-modal.component/admin-action-modal.component';
import {CdkCopyToClipboard} from '@angular/cdk/clipboard';


export interface ProjectDetailData {
  project: ProjectDomain;
  isAdminView?: boolean;
}

@Component({
  selector: 'app-project-detail-modal',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    ProjectModalLayoutComponent,
    TranslatePipe,
    MatFormFieldModule, // Zde musí být MODUL
    MatInputModule,
    FormsModule,
    CdkCopyToClipboard,
  ],
  templateUrl: './project-detail-modal.component.html',
  styleUrls: ['./project-detail-modal.component.css']
})
export class ProjectDetailModalComponent {

  public project: ProjectDomain;
  public downloadingType: 'original' | 'translated' | null = null;
  protected targetLanguageName: string = '';
  public isAdminView: boolean = false;

  // Vlastnosti 'adminMessage', 'isSending', 'isClosing' byly ODSTRANĚNY

  readonly downloadOriginalMutation = useDownloadOriginalMutation();
  readonly downloadTranslatedMutation = useDownloadTranslatedMutation();

  constructor(
    public dialogRef: MatDialogRef<ProjectDetailModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectDetailData,
    private toastr: ToastrService,
    protected languageService: LanguageListService,
    private translationService: TranslateService,
    private dialog: MatDialog // Přidali jsme MatDialog pro otevírání nového modálu
  ) {
    this.project = data.project;
    this.isAdminView = data.isAdminView ?? false;
    this.targetLanguageName = this.languageService.getLanguageName(this.project.targetLanguage);

    // Předvyplnění zprávy (už není potřeba, řeší to nový modál)
    // if (this.project.feedback) { ... }
  }

  onClose(): void {
    this.dialogRef.close();
  }

  // Metody onDownload a triggerFileDownload zůstávají beze změny
  async onDownload(type: 'original' | 'translated'): Promise<void> {
    if (this.downloadingType) return;
    if (type === 'translated' && !this.project.translatedFileName) {
      let fileNotAvailable = this.translationService.instant('projectDetailModal.notifications.translatedFileNotAvailable');
      this.toastr.warning(fileNotAvailable);
      return;
    }
    this.downloadingType = type;
    try {
      const mutation = (type === 'original')
        ? this.downloadOriginalMutation
        : this.downloadTranslatedMutation;
      const blob = await mutation.mutateAsync(this.project.id);
      const filename = (type === 'original')
        ? this.project.originalFileName
        : this.project.translatedFileName;
      if (!filename) {
        throw new Error('Filename is not available.');
      }
      this.triggerFileDownload(blob, filename);
      let successMessage = this.translationService.instant('projectDetailModal.notifications.downloadSuccess', { fileName: filename });
      this.toastr.success(successMessage);
    } catch (error) {
      console.error('Download failed', error);
      let downloadErrorMessage = this.translationService.instant('projectDetailModal.notifications.downloadError');
      this.toastr.error(downloadErrorMessage);
    } finally {
      this.downloadingType = null;
    }
  }
  private triggerFileDownload(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }


  onIdCopied() {
    let copySuccessMsg = this.translationService.instant('projectDetailModal.notifications.idCopySuccess');
    this.toastr.success(copySuccessMsg);
  }
}
