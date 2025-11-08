import {Component, Inject} from '@angular/core';
import {CommonModule, TitleCasePipe} from '@angular/common';
import {ProjectDomain} from '@core/models/project.model';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ToastrService} from 'ngx-toastr';
import {LanguageService} from '@core/services/language.service';
import {useDownloadOriginalMutation, useDownloadTranslatedMutation,} from '@api/queries/project.query';

export interface ProjectDetailData {
  project: ProjectDomain;
}

@Component({
  selector: 'app-project-detail-modal',
  standalone: true,
  imports: [
    CommonModule,
    TitleCasePipe,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './project-detail-modal.component.html',
  styleUrls: ['./project-detail-modal.component.css']
})
export class ProjectDetailModalComponent {

  public project: ProjectDomain;
  public downloadingType: 'original' | 'translated' | null = null;
  public targetLanguageName: string;
  public targetLanguageTag: string;
  public sourceLanguageTag: string = 'en';


  readonly downloadOriginalMutation = useDownloadOriginalMutation();
  readonly downloadTranslatedMutation = useDownloadTranslatedMutation();

  constructor(
    public dialogRef: MatDialogRef<ProjectDetailModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectDetailData,
    private toastr: ToastrService,
    private languageService: LanguageService,
  ) {
    this.project = data.project;
    this.targetLanguageName = this.languageService.getLanguageName(this.project.targetLanguage);
    this.targetLanguageTag = this.project.targetLanguage;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  async onDownload(type: 'original' | 'translated'): Promise<void> {
    if (this.downloadingType) return;

    // ZÁKAZ VOLÁNÍ ENDPOINTU, POKUD SOUBOR NENÍ K DISPOZICI
    if (type === 'translated' && !this.project.translatedFileName) {
      this.toastr.warning('Translated file is not yet available for download.');
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
      this.toastr.success(`Downloaded ${filename} successfully!`);

    } catch (error) {

      console.error('Download failed', error);
      this.toastr.error('Failed to download file. Please try again.');

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
}
