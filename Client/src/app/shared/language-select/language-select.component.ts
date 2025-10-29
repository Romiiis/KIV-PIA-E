import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Language {
  code: string;
  name: string;
}

@Component({
  selector: 'app-language-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './language-select.component.html',
  styleUrls: ['./language-select.component.css']
})
export class LanguageSelectComponent implements OnInit {
  @Input() multiple = false;
  @Output() selected = new EventEmitter<string | string[]>();

  languages: Language[] = [];
  filteredLanguages: Language[] = [];
  selectedLanguages: Language[] = [];
  searchTerm = '';
  showDropdown = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<Language[]>('languages.json').subscribe(data => {
      this.languages = data;
      this.filteredLanguages = data;
    });
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase();
    this.filteredLanguages = this.languages.filter(l =>
      l.name.toLowerCase().includes(term) || l.code.toLowerCase().includes(term)
    );
    this.showDropdown = true;
  }

  selectLanguage(lang: Language) {
    if (this.isSelected(lang)) {
      this.removeLanguage(lang);
      return;
    }
    this.selectedLanguages.push(lang);
    this.selected.emit(this.selectedLanguages.map(l => l.code));
    this.searchTerm = '';
  }

  removeLanguage(lang: Language) {
    this.selectedLanguages = this.selectedLanguages.filter(l => l.code !== lang.code);
    this.selected.emit(this.selectedLanguages.map(l => l.code));
  }

  isSelected(lang: Language): boolean {
    return this.selectedLanguages.some(l => l.code === lang.code);
  }

  focusInput(input: HTMLInputElement) {
    input.focus();
  }
}
