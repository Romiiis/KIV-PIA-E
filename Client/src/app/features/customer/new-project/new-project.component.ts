import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {LanguageSelectComponent} from '@shared/language-select/language-select.component';

interface Language {
  code: string;
  name: string;
}

@Component({
  selector: 'app-customer-new-project',
  templateUrl: './new-project.component.html',
  imports: [
    ReactiveFormsModule,
    LanguageSelectComponent
  ],
  styleUrls: ['./new-project.component.css']
})
export class NewProjectComponent implements OnInit{
  @Output() create = new EventEmitter<any>();
  projectForm: FormGroup;
  languages: Language[] = [];
  filteredLanguages: Language[] = [];

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.projectForm = this.fb.group({
      file: [null, Validators.required],
      targetLang: ['', Validators.required]
    });




  }

  ngOnInit(): void {
    this.http.get<Language[]>('/languages.json').subscribe(data => {
      this.languages = data;
      this.filteredLanguages = data;
    });
  }

  onSearch(term: string) {
    const t = term.toLowerCase();
    this.filteredLanguages = this.languages.filter(l =>
      l.name.toLowerCase().includes(t) || l.code.toLowerCase().includes(t)
    );
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.projectForm.patchValue({ file: input.files[0] });
    }
  }

  submitProject() {
    if (this.projectForm.valid) {
      const file = this.projectForm.value.file;
      const project = {
        id: Date.now(),
        filename: file.name,
        targetLang: this.projectForm.value.targetLang,
        status: 'waiting'
      };
      this.create.emit(project);
      this.projectForm.reset();
    }
  }
}
