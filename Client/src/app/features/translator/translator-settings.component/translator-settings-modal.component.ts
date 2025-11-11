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
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

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
    MatProgressSpinnerModule,
    TranslatePipe
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
    private toastr: ToastrService,
    private translationService: TranslateService
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
      let errorMsg = this.translationService.instant('translatorSettingsModal.notifications.userInfoLoadError');
      this.toastr.error(errorMsg);
      return;
    }

    this.isLoading = true;
    const { languages } = this.form.value;

    if (languages.length === 0) {
      let errorMsg = this.translationService.instant('translatorSettingsModal.notifications.selectAtLeastOneLanguage');
      this.toastr.error(errorMsg);
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
          let successMsg = this.translationService.instant('translatorSettingsModal.notifications.settingsSavedSuccess');
          this.toastr.success(successMsg);
          this.form.markAsPristine();
          this.dialogRef.close('saved');
        },
        onError: (err) => {
          console.error('Failed to update settings:', err);

          let errorMsg = this.translationService.instant('translatorSettingsModal.notifications.settingsSavedError');
          this.toastr.error(errorMsg);
        },
        onSettled: () => {
          this.isLoading = false;
        }
      }
    );
  }
}
