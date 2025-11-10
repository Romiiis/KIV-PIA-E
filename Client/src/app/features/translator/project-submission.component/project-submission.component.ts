import {Component, Inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {ProjectDomain} from '@core/models/project.model';
import {ToastrService} from 'ngx-toastr';
import {ProjectModalLayoutComponent} from '@shared/project-modal-layout/project-modal-layout.component';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {useDownloadOriginalMutation} from '@api/queries/project.query';
import {MatTooltipModule} from '@angular/material/tooltip';
import {useUploadTranslatedMutation} from '@api/queries/workflow.query';


export interface ProjectSubmissionData {
  project: ProjectDomain;
}

@Component({
  selector: 'app-project-submission',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    ProjectModalLayoutComponent
  ],
  templateUrl: './project-submission.component.html',
  styleUrls: ['./project-submission.component.css']
})
export class ProjectSubmissionComponent implements OnInit {

  project: ProjectDomain;
  uploadForm: FormGroup;
  isLoading = false;
  isDownloading = false;


  readonly submitMutation = useUploadTranslatedMutation();
  readonly downloadOriginalMutation = useDownloadOriginalMutation();

  constructor(
    public dialogRef: MatDialogRef<ProjectSubmissionComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectSubmissionData,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.project = data.project;
    this.uploadForm = this.fb.group({
      file: [null, Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      this.uploadForm.patchValue({file: file});
      this.uploadForm.get('file')?.markAsTouched();
    }
  }

  async onDownloadOriginal(): Promise<void> {
    if (this.isDownloading) return;
    this.isDownloading = true;

    try {
      const blob = await this.downloadOriginalMutation.mutateAsync(this.project.id);
      this.triggerFileDownload(blob, this.project.originalFileName);
      this.toastr.success(`Downloaded ${this.project.originalFileName}`);
    } catch (error) {
      this.toastr.error('Failed to download original file.');
    } finally {
      this.isDownloading = false;
    }
  }

  async submitTranslation(): Promise<void> {
    if (!this.uploadForm.valid) {
      this.toastr.warning('Please select a file to upload.');
      return;
    }
    if (this.isLoading) return;

    this.isLoading = true;
    const {file} = this.uploadForm.value;

    try {
      await this.submitMutation.mutateAsync({
        id: this.project.id,
        file: file
      });
      this.toastr.success('Translation submitted successfully!');
      this.dialogRef.close('submitted');
    } catch (error) {
      this.toastr.error('Failed to submit translation. Please try again.');
    } finally {
      this.isLoading = false;
    }
  }

  onClose(): void {
    this.dialogRef.close();
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
