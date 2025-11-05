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

/**
 * Component for initializing user role and preferences after registration.
 */
export class InitUserComponent {

  // Reactive form for user role and language selection
  form: FormGroup;
  // Selected languages for translator role
  selectedLanguages: string[] = [];

  /** Mutation hook to change user role */
  readonly changeRoleMutation = useChangeUserRoleMutation();


  /** Expose UserRoleDomain enum to template */
  protected readonly UserRoleDomain = UserRoleDomain;


  /**
   * Constructor to initialize form and inject dependencies
   * @param fb FormBuilder
   * @param auth AuthManager
   * @param router Router
   * @param toastr ToastrService
   */
  constructor(
    private fb: FormBuilder,
    private auth: AuthManager,
    private router: Router,
    private toastr: ToastrService,
  ) {

    // Initialize the reactive form with default values
    this.form = this.fb.group({
      role: [UserRoleDomain.CUSTOMER],
      languages: [[]],
    });
  }

  /**
   * Select user role and reset languages if customer
   * @param role Selected user role
   */
  selectRole(role: UserRoleDomain.CUSTOMER | UserRoleDomain.TRANSLATOR) {

    // Update form role value
    this.form.get('role')?.setValue(role);

    // Reset languages if role is customer
    if (role === UserRoleDomain.CUSTOMER) {
      this.selectedLanguages = [];
      this.form.get('languages')?.setValue([]);
    }
  }

  /**
   * Handle language selection changes
   * @param selected Selected languages
   */
  onLanguagesSelected(selected: string[] | string) {
    // Ensure selected is an array
    const langs = Array.isArray(selected) ? selected : [selected];
    // Update selected languages and form value
    this.form.get('languages')?.setValue(langs);
  }

  /**
   * Save the selected role and languages, then navigate accordingly
   */
  saveSelection() {

    // Get form data and user ID
    const userData = this.form.value;

    // Get current user ID from auth manager
    const userId = this.auth.user()?.id;

    // Ensure user ID is defined
    if (!userId) {
      console.error('User ID is undefined. Cannot change role.');
      return;
    }

    // Call mutation to change user role
    this.changeRoleMutation.mutate(
      {
        id: userId,
        role: userData.role,
        languages: userData.languages,
      },
      {
        onSuccess: async () => {
          this.toastr.success('User role and preferences updated successfully!', 'Success');
          // Refresh user data in auth manager
          await this.auth.refreshUserData();

          // Get updated user and navigate based on role
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
