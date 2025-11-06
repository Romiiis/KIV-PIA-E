import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {CommonModule} from '@angular/common';

import {LanguageSelectComponent} from '@shared/language-select/language-select.component';
import {useCreateProjectMutation} from '@api/queries/project.query';

@Component({
  selector: 'app-customer-new-project',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LanguageSelectComponent,
    MatDialogModule,
    MatButtonModule,
    MatProgressSpinnerModule
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
    private http: HttpClient,
    private toastr: ToastrService
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

  async submitProject(): Promise<void> { // <-- Měníme na async
    if (!this.projectForm.valid) {
      return;
    }

    this.isLoading = true;
    const {file, targetLang} = this.projectForm.value;

    try {
      // Vytvoříme pole všech mutačních Promise
      const mutationPromises = targetLang.map((langCode: string) =>
        this.createMutation.mutateAsync({
          languageCode: langCode,
          file: file
        })
      );

      await Promise.all(mutationPromises);

      this.toastr.success(`Successfully created ${targetLang.length} projects!`);
      this.dialogRef.close('created');

    } catch (error) {
      this.toastr.error('Failed to create one or more projects. Please try again.');
    } finally {
      this.isLoading = false;
    }
  }
}
