import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {LanguageSelectComponent} from '@shared/language-select/language-select.component';
import {useCreateProjectMutation} from '@api/queries/project.query';
import {ToastrService} from 'ngx-toastr';

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
export class NewProjectComponent implements OnInit {
  projectForm: FormGroup;
  languages: Language[] = [];
  filteredLanguages: Language[] = [];

  @Output() close = new EventEmitter<void>();


  private createMutation = useCreateProjectMutation();

  constructor(private fb: FormBuilder, private http: HttpClient, private toastr: ToastrService) {
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
      this.projectForm.patchValue({file: input.files[0]});
    }
  }


  submitProject() {
    if (this.projectForm.valid) {
      const file = this.projectForm.value.file;
      this.createMutation.mutate(
        {
          languageCode: this.projectForm.value.targetLang,
          content: file
        },
        {
          onSuccess: (data) => {
            this.toastr.success('Project created successfully!');
            // Reset form or provide feedback to user
            this.close.emit(); // ✅ zavře modal
          },
          onError: (error) => {
            this.toastr.error('Failed to create project. Please try again.');
          }
        }
      );
    }
  }
}
