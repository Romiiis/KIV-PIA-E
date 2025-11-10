import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ToastrService } from 'ngx-toastr';
import { AuthManager } from '@core/auth/auth.manager';
import { MatDialogModule, MatDialogRef, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { LanguageSelectComponent } from '@shared/language-select/language-select.component';
import {useReplaceTranslatorLanguagesMutation} from '@api/queries/translators.query';

@Component({
  selector: 'app-translator-settings-modal',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LanguageSelectComponent,
    MatDialogModule,
    MatDialogContent,
    MatDialogActions,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './translator-settings-modal.component.html',
  styleUrls: ['./translator-settings-modal.component.css']
})
export class TranslatorSettingsModalComponent implements OnInit {

  form: FormGroup;
  isLoading = false;

  readonly updateSettingsMutation = useReplaceTranslatorLanguagesMutation();

  constructor(
    public dialogRef: MatDialogRef<TranslatorSettingsModalComponent>,
    private fb: FormBuilder,
    private auth: AuthManager,
    private toastr: ToastrService
  ) {
    this.form = this.fb.group({
      languages: [[] as string[]]
    });
  }

  ngOnInit(): void {
    const currentUserLanguages = this.auth.user()?.languages || [];

    this.form.patchValue({
      languages: currentUserLanguages
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  async saveSettings(): Promise<void> {
    if (this.form.invalid || this.isLoading) {
      return;
    }

    const currentUser = this.auth.user();
    if (!currentUser) {
      this.toastr.error('User not found. Please log in again.');
      return;
    }

    this.isLoading = true;
    const { languages } = this.form.value;

    if (languages.length === 0) {
      this.toastr.error('Please select at least one language.');
      this.isLoading = false;
      return;
    }

    this.updateSettingsMutation.mutate(
      {
        id: currentUser.id,
        languages: languages
      },
      {
        onSuccess: async () => {
          await this.auth.refreshUserData();
          this.toastr.success('Your language settings have been updated!');
          this.form.markAsPristine();
          this.dialogRef.close('saved');
        },
        onError: (err) => {
          console.error('Failed to update settings:', err);
          this.toastr.error('Failed to update settings. Please try again.');
        },
        onSettled: () => {
          this.isLoading = false;
        }
      }
    );
  }
}
