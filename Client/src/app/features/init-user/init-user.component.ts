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

@Component({
  selector: 'app-init-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LanguageSelectComponent],
  templateUrl: './init-user.component.html',
  styleUrls: ['./init-user.component.css'],
})
export class InitUserComponent {

  form: FormGroup;
  // TATO PROMĚNNÁ JE ZBYTEČNÁ, ODEBRÁNO
  // selectedLanguages: string[] = [];

  readonly changeRoleMutation = useChangeUserRoleMutation();
  protected readonly UserRoleDomain = UserRoleDomain;

  constructor(
    private fb: FormBuilder,
    private auth: AuthManager,
    private router: Router,
    private toastr: ToastrService,
  ) {
    this.form = this.fb.group({
      role: [UserRoleDomain.CUSTOMER],
      languages: [[]],
    });
  }

  selectRole(role: UserRoleDomain.CUSTOMER | UserRoleDomain.TRANSLATOR) {
    this.form.get('role')?.setValue(role);

    // Stále resetujeme, to je v pořádku
    if (role === UserRoleDomain.CUSTOMER) {
      this.form.get('languages')?.setValue([]);
    }
  }

  saveSelection() {
    // Teď už userData.languages bude pole stringů, např. ['en', 'fr']
    const userData = this.form.value;
    const userId = this.auth.user()?.id;

    console.log('Saving user role selection:', userData);

    if (!userId) {
      console.error('User ID is undefined. Cannot change role.');
      return;
    }

    this.changeRoleMutation.mutate(
      {
        id: userId,
        role: userData.role,
        //
        // ZDE JE KLÍČOVÁ ZMĚNA:
        // Bylo: userData.languages.selectedLanguages
        // Nově: userData.languages
        //
        languages: userData.languages
      },
      {
        onSuccess: async () => {
          this.toastr.success('User role and preferences updated successfully!', 'Success');
          await this.auth.refreshUserData();
          const updatedUser = this.auth.user();
          if (updatedUser) {
            console.log('Navigating based on role:', updatedUser.role);
            AuthRoutingHelper.navigateToRole(this.router, updatedUser.role).then();
          }
        },
      }
    );
  }
}
