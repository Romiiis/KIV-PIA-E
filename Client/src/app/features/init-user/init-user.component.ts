import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {LanguageSelectComponent} from '@shared/language-select/language-select.component';
import {useChangeUserRoleMutation} from '@api/queries/users.query';
import {AuthManager} from '@core/auth/auth.manager';
import {Router} from '@angular/router';
import {UserRoleDomain} from '@core/models/userRole.model';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-init-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LanguageSelectComponent, TranslatePipe, MatIcon],
  templateUrl: './init-user.component.html',
  styleUrls: ['./init-user.component.css'],
})
export class InitUserComponent {


  protected form: FormGroup;

  readonly changeRoleMutation = useChangeUserRoleMutation();

  protected readonly UserRoleDomain = UserRoleDomain;


  constructor(
    private fb: FormBuilder,
    private auth: AuthManager,
    private router: Router,
    private toastr: ToastrService,
    protected translate: TranslateService
  ) {


    this.form = this.fb.group({
      role: [UserRoleDomain.CUSTOMER],
      languages: [[]],
    });
  }



  selectRole(role: UserRoleDomain.CUSTOMER | UserRoleDomain.TRANSLATOR) {
    this.form.get('role')?.setValue(role);

    if (role === UserRoleDomain.CUSTOMER) {
      this.form.get('languages')?.setValue([]);
    }
  }


  saveSelection() {

    const userData = this.form.value;
    const userId = this.auth.user()?.id;


    if (!userId) {
      console.error('User ID is undefined. Cannot change role.');
      return;
    }


    this.changeRoleMutation.mutate(
      {
        id: userId,
        role: userData.role,
        languages: userData.languages
      },
      {
        onSuccess: async () => {

          let successInitText = this.translate.instant('initPage.notifications.initSuccess_text')
          let successTitleText = this.translate.instant('initPage.notifications.initSuccess_title')
          this.toastr.success(successInitText, successTitleText);

          await this.auth.refreshUserData();

          const updatedUser = this.auth.user();

          if (updatedUser) {
            AuthRoutingHelper.navigateToRole(this.router, updatedUser.role).then();
          }

        },
      }
    );
  }
}
