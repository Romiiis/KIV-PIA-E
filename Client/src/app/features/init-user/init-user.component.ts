import {Component, inject, NgZone} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {LanguageSelectComponent} from '@shared/language-select/language-select.component';
import {useChangeUserRoleMutation} from '@api/queries/user.query';
import {AuthService} from '@core/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-init-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LanguageSelectComponent],
  templateUrl: './init-user.component.html',
  styleUrls: ['./init-user.component.css'],
})
export class InitUserComponent {
  form: FormGroup;
  selectedLanguages: string[] = [];

  readonly changeRoleMutation = useChangeUserRoleMutation();
  private zone = inject(NgZone);


  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      role: ['customer'],
      languages: [[]],
    });
  }

  selectRole(role: 'customer' | 'translator') {
    this.form.get('role')?.setValue(role);

    if (role === 'customer') {
      this.selectedLanguages = [];
      this.form.get('languages')?.setValue([]);
    }
  }

  onLanguagesSelected(selected: string[] | string) {
    console.log('Languages selected:', selected);
    const langs = Array.isArray(selected) ? selected : [selected];
    this.form.get('languages')?.setValue(langs);
  }

  saveSelection() {
    const userData = this.form.value;
    console.log('User initialized as:', userData);
    // TODO: call userService.saveUserInit(userData)

    let userId = this.auth.user()?.id;
    if (!userId) {
      console.error('User ID is undefined. Cannot change role.');
      return;
    }

    this.changeRoleMutation.mutate(
      {id: userId, role: userData.role.toUpperCase(), languages: userData.languages},
      {
        onSuccess: async () => {
          console.log('✅ User role changed successfully.');

          // Refetch user info (pull new role from backend)
          const updatedUser = await this.auth.refreshUser();
          console.log('🧠 Updated user after role change:', updatedUser);

          if (updatedUser) {
            const targetPath = this.auth.resolvePath(updatedUser.role);
            console.log('➡️ Navigating to:', targetPath);
            this.router.navigate([targetPath]);
          }
        }
      });
  }
}
