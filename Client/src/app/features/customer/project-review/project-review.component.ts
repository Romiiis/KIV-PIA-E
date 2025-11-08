import { Component, Inject } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProjectDomain } from '@core/models/project.model';
import { ToastrService } from 'ngx-toastr';
import { LanguageService } from '@core/services/language.service';

// API mutace
import { useApproveTranslatedMutation, useRejectTranslatedMutation } from '@api/queries/workflow.query';
import { useDownloadOriginalMutation, useDownloadTranslatedMutation } from '@api/queries/project.query';

export interface ProjectReviewData {
  project: ProjectDomain;
}

@Component({
  selector: 'app-project-review',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    TitleCasePipe,
    MatTooltipModule
  ],
  templateUrl: './project-review.component.html',
  styleUrls: ['./project-review.component.css']
})
export class ProjectReviewComponent {

  public project: ProjectDomain;
  public feedbackForm: FormGroup;
  public targetLanguageName: string;
  public targetLanguageTag: string;
  public sourceLanguageTag: string = 'en';

  isRejecting = false;
  isLoading = false;
  isDownloading = false;

  // Mutace pro schválení/zamítnutí
  readonly approveMutation = useApproveTranslatedMutation();
  readonly rejectMutation = useRejectTranslatedMutation();

  // Mutace pro stahování
  readonly downloadOriginalMutation = useDownloadOriginalMutation();
  readonly downloadTranslatedMutation = useDownloadTranslatedMutation();

  constructor(
    public dialogRef: MatDialogRef<ProjectReviewComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectReviewData,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private languageService: LanguageService
  ) {
    this.project = data.project;
    this.feedbackForm = this.fb.group({
      feedback: ['', [Validators.maxLength(500)]],
    });

    this.targetLanguageName = this.languageService.getLanguageName(this.project.targetLanguage);
    this.targetLanguageTag = this.project.targetLanguage.toLowerCase();
  }

  toggleRejectMode(): void {
    this.isRejecting = !this.isRejecting;
    if (!this.isRejecting) {
      this.feedbackForm.get('feedback')?.setValue('');
    }
  }

  async onApprove(): Promise<void> {
    this.isLoading = true;
    try {
      await this.approveMutation.mutateAsync(this.project.id);
      this.toastr.success('Project approved and closed successfully.', 'Success');
      this.dialogRef.close('approved');
    } catch (error) {
      this.toastr.error('Failed to approve project. Try again.', 'Error');
    } finally {
      this.isLoading = false;
    }
  }

  async onReject(): Promise<void> {
    if (this.isRejecting && this.feedbackForm.valid) {
      this.isLoading = true;
      const feedbackText = this.feedbackForm.get('feedback')?.value || '';

      if (feedbackText.trim() === '') {
        this.toastr.warning('Please provide feedback when rejecting the translation.', 'Required');
        this.isLoading = false;
        return;
      }

      try {
        await this.rejectMutation.mutateAsync({
          id: this.project.id,
          feedback: feedbackText
        });
        this.toastr.info('Translation rejected. Feedback sent to administrator.', 'Rejected');
        this.dialogRef.close('rejected');
      } catch (error) {
        this.toastr.error('Failed to reject project. Try again.', 'Error');
      } finally {
        this.isLoading = false;
      }
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

  // --- LOGIKA STAHOVÁNÍ ---

  async onDownload(): Promise<void> {
    if (this.isDownloading) return;

    if (!this.project.translatedFileName) {
      this.toastr.warning('Translated file is not yet available for download.');
      return;
    }

    this.isDownloading = true;

    try {
      const blob = await this.downloadTranslatedMutation.mutateAsync(this.project.id);
      this.triggerFileDownload(blob, this.project.translatedFileName);
      this.toastr.success(`Downloaded ${this.project.translatedFileName} successfully!`);
    } catch (error) {
      console.error('Download failed', error);
      this.toastr.error('Failed to download file. Please try again.');
    } finally {
      this.isDownloading = false;
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
