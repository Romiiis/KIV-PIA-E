import {Component, Inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatTooltipModule} from '@angular/material/tooltip';
import {ProjectDomain} from '@core/models/project.model';
import {ToastrService} from 'ngx-toastr';

import {useApproveTranslatedMutation, useRejectTranslatedMutation} from '@api/queries/workflow.query';
import {useDownloadTranslatedMutation} from '@api/queries/project.query';
import {ProjectModalLayoutComponent} from '@shared/project-modal-layout/project-modal-layout.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';


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
    MatTooltipModule,
    ProjectModalLayoutComponent,
    TranslatePipe,
  ],
  templateUrl: './project-review.component.html',
  styleUrls: ['./project-review.component.css']
})
export class ProjectReviewComponent {

  public project: ProjectDomain;
  public feedbackForm: FormGroup;


  isRejecting = false;
  isLoading = false;
  isDownloading = false;


  readonly approveMutation = useApproveTranslatedMutation();
  readonly rejectMutation = useRejectTranslatedMutation();
  readonly downloadTranslatedMutation = useDownloadTranslatedMutation();


  constructor(
    public dialogRef: MatDialogRef<ProjectReviewComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectReviewData,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private translationService: TranslateService
  ) {
    this.project = data.project;
    this.feedbackForm = this.fb.group({
      feedback: ['', [Validators.maxLength(500)]],
    });
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

      let approvedText = this.translationService.instant("projectReviewModal.notifications.approvalSuccess");
      let successTitle = this.translationService.instant("global.success");

      this.toastr.success(approvedText, successTitle);
      this.dialogRef.close('approved');
    } catch (error) {
      let approvalErrorText = this.translationService.instant("projectReviewModal.notifications.approvalError");
      let errorTitle = this.translationService.instant("global.error");

      this.toastr.error(approvalErrorText, errorTitle);
    } finally {
      this.isLoading = false;
    }
  }

  async onReject(): Promise<void> {
    if (this.isRejecting && this.feedbackForm.valid) {
      this.isLoading = true;
      const feedbackText = this.feedbackForm.get('feedback')?.value || '';

      if (feedbackText.trim() === '') {
        let requiredTitle = this.translationService.instant("global.required");
        let requiredText = this.translationService.instant("projectReviewModal.notifications.feedbackRequired");

        this.toastr.warning(requiredText, requiredTitle);
        this.isLoading = false;
        return;
      }

      try {
        await this.rejectMutation.mutateAsync({
          id: this.project.id,
          feedback: feedbackText
        });
        let rejectedTitle = this.translationService.instant("global.rejected");
        let rejectedText = this.translationService.instant("projectReviewModal.notifications.rejectionSuccess");

        this.toastr.info(rejectedText, rejectedTitle);
        this.dialogRef.close('rejected');


      } catch (error) {
        let errorTitle = this.translationService.instant("global.error");
        this.toastr.error('Failed to reject project. Try again.', errorTitle);
      } finally {
        this.isLoading = false;
      }
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

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
