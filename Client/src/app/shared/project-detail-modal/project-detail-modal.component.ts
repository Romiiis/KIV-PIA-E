import {Component, Inject} from '@angular/core';
import {CommonModule, TitleCasePipe} from '@angular/common';
import {ProjectDomain} from '@core/models/project.model';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
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
  ],
  templateUrl: './project-detail-modal.component.html',
  styleUrls: ['./project-detail-modal.component.css']
})
export class ProjectDetailModalComponent {

  public project: ProjectDomain;
  public downloadingType: 'original' | 'translated' | null = null;

  protected targetLanguageName: string = '';

  public isAdminView: boolean = false;
  public adminMessage: string = '';
  public isSending: boolean = false;
  public isClosing: boolean = false;


  readonly downloadOriginalMutation = useDownloadOriginalMutation();
  readonly downloadTranslatedMutation = useDownloadTranslatedMutation();

  constructor(
    public dialogRef: MatDialogRef<ProjectDetailModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectDetailData,
    private toastr: ToastrService,
    protected languageService: LanguageListService,
    private translationService: TranslateService
  ) {
    this.project = data.project;
    this.isAdminView = data.isAdminView ?? false;
    this.targetLanguageName = this.languageService.getLanguageName(this.project.targetLanguage);
    if (this.project.feedback) {
      this.adminMessage = `Odpověď na feedback:\n"${this.project.feedback.text}"\n\n------------------\n`;
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

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


  // TODO - nove funkce pro admina - odeslani zpravy a uzavreni projektu

  /**
   * (Simulace) Odešle zprávu zákazníkovi/překladateli
   */
  async onAdminSendMessage(): Promise<void> {
    if (this.isSending || !this.adminMessage.trim()) return;
    this.isSending = true;

    try {
      // Zde byste volali reálnou mutaci:
      // await this.sendMessageMutation.mutateAsync({
      //   projectId: this.project.id,
      //   message: this.adminMessage
      // });

      // Simulace
      await new Promise(resolve => setTimeout(resolve, 750));
      this.toastr.success('Zpráva byla odeslána (simulace).');
      this.adminMessage = ''; // Vyčistit zprávu

    } catch (error) {
      this.toastr.error('Odeslání zprávy selhalo.');
      console.error('Send message failed', error);
    } finally {
      this.isSending = false;
    }
  }

  /**
   * (Simulace) Uzavře projekt z pohledu admina
   */
  async onAdminCloseProject(): Promise<void> {
    if (this.isClosing) return;
    this.isClosing = true;

    try {
      // Zde byste volali reálnou mutaci:
      // await this.closeProjectMutation.mutateAsync(this.project.id);

      // Simulace
      await new Promise(resolve => setTimeout(resolve, 750));
      this.toastr.success('Projekt byl úspěšně uzavřen (simulace).');

      // Zavřeme modál a vrátíme 'projectUpdated', aby se seznam na stránce admina obnovil
      this.dialogRef.close('projectUpdated');

    } catch (error) {
      this.toastr.error('Uzavření projektu selhalo.');
      console.error('Close project failed', error);
    } finally {
      this.isClosing = false;
    }
  }

}
