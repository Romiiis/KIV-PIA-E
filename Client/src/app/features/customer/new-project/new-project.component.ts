import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {CommonModule} from '@angular/common';

import {LanguageSelectComponent} from '@shared/language-select/language-select.component';
import {useCreateProjectMutation} from '@api/queries/project.query';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-customer-new-project',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LanguageSelectComponent,
    MatDialogModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    TranslatePipe
  ],
  templateUrl: './new-project.component.html',
  styleUrls: ['./new-project.component.css']
})
export class NewProjectComponent implements OnInit {
  projectForm: FormGroup;
  isLoading = false;

  private createMutation = useCreateProjectMutation();


  constructor(
    public dialogRef: MatDialogRef<NewProjectComponent>,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private translationService: TranslateService
  ) {
    this.projectForm = this.fb.group({
      file: [null, Validators.required],
      targetLang: [[] as string[], [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.projectForm.patchValue({file: input.files[0]});
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  async submitProject(): Promise<void> {
    if (!this.projectForm.valid) {
      return;
    }

    this.isLoading = true;
    const {file, targetLang} = this.projectForm.value;

    try {


      let project = await this.createMutation.mutateAsync({
        languageCode: targetLang,
        file: file
      });

      let projectCreatedSuccessText = this.translationService.instant('createProjectModal.notifications.projectCreatedSuccess')
      this.toastr.success(projectCreatedSuccessText);
      this.dialogRef.close({result:'created', project: project});

    } catch (error) {
      let projectCreatedErrorText = this.translationService.instant('createProjectModal.notifications.projectCreatedError')
      this.toastr.error(projectCreatedErrorText);
    } finally {
      this.isLoading = false;
    }
  }
}
