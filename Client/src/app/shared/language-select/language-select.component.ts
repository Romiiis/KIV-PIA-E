import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Language {
  code: string;
  name: string;
}

@Component({
  selector: 'app-language-select',
  imports: [CommonModule, FormsModule],
  templateUrl: './language-select.component.html',
  styleUrls: ['./language-select.component.css']
})
export class LanguageSelectComponent implements OnInit {
  @Output() selected = new EventEmitter<string>();

  languages: Language[] = [];
  filteredLanguages: Language[] = [];
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
    this.searchTerm = lang.name;
    this.showDropdown = false;
    this.selected.emit(lang.code);
  }
}
