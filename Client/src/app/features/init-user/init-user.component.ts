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
  availableLanguages = ['English', 'Spanish', 'French', 'German', 'Czech', 'Polish'];
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

  toggleLanguage(lang: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedLanguages.push(lang);
    } else {
      this.selectedLanguages = this.selectedLanguages.filter(l => l !== lang);
    }
    this.form.get('languages')?.setValue(this.selectedLanguages);
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
