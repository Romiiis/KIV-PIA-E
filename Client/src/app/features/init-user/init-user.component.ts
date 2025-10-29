import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import {LanguageSelectComponent} from '@shared/language-select/language-select.component';

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

  constructor(private fb: FormBuilder) {
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
  }
}
