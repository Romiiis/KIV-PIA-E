import {Component, Inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {ProjectDomain} from '@core/models/project.model';
import {ToastrService} from 'ngx-toastr';
import {FormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {MatRadioModule} from '@angular/material/radio';
import {useMail} from '@api/queries/mail.query';
import {useListProjectsMutation} from '@api/queries/project.query';
export interface AdminActionData {
  project: ProjectDomain;
}

@Component({
  selector: 'app-admin-action-modal',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    TranslatePipe,
    MatRadioModule
  ],
  templateUrl: './admin-action-modal.component.html',
  styleUrls: ['./admin-action-modal.component.css']
})
export class AdminActionModalComponent {

  public project: ProjectDomain;
  public adminMessage: string = '';
  public isSending: boolean = false;

  public messageRecipient: 'both' | 'customer' | 'translator' = 'both';
  public hasTranslator: boolean = false;

  readonly mailMutation = useMail();

  constructor(
    public dialogRef: MatDialogRef<AdminActionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AdminActionData,
    private toastr: ToastrService,
    private translate: TranslateService
  ) {
    this.project = data.project;
    this.hasTranslator = !!this.project.translator;

    if (!this.hasTranslator) {
      this.messageRecipient = 'customer';
    }

    if (!this.project.feedback) {
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

  async onAdminSendMessage(): Promise<void> {
    if (this.isSending || !this.adminMessage.trim()) return;
    this.isSending = true;

    try {

      let sendCustomer = this.messageRecipient === 'customer' || this.messageRecipient === 'both';
      let sendTranslator = this.messageRecipient === 'translator' || this.messageRecipient === 'both';

      await this.mailMutation.mutateAsync({projectId: this.project.id, textToSend: this.adminMessage, sendCustomer: sendCustomer, sendTranslator: sendTranslator});

      let targetKey = 'adminActionModal.targetBoth';

      if (this.messageRecipient === 'customer') targetKey = 'adminActionModal.targetCustomer';
      if (this.messageRecipient === 'translator') targetKey = 'adminActionModal.targetTranslator';


      const targetText = this.translate.instant(targetKey);
      this.toastr.success(this.translate.instant('adminActionModal.toastSendSuccess', {target: targetText}));
      this.adminMessage = '';
      this.dialogRef.close();
    } catch (error) {
      this.toastr.error(this.translate.instant('adminActionModal.toastSendError'));
      console.error('Send message failed', error);
    } finally {
      this.isSending = false;
    }
  }
}
