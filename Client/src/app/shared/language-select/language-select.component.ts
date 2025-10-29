import {Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild} from '@angular/core';
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
  @Input() multiple = true;
  @Output() selected = new EventEmitter<string[]>();


  languages: Language[] = [];
  filteredLanguages: Language[] = [];
  selectedLanguages: Language[] = [];
  searchTerm = '';
  showDropdown = false;



  constructor(private http: HttpClient, private elementRef: ElementRef) {}

  ngOnInit(): void {
    this.http.get<Language[]>('languages.json').subscribe((data) => {
      this.languages = data;
      this.filteredLanguages = data;
    });
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredLanguages = this.languages.filter(
      (l) =>
        l.name.toLowerCase().includes(term) ||
        l.code.toLowerCase().includes(term)
    );
    this.showDropdown = true;
  }

  selectLanguage(lang: Language) {
    if (this.isSelected(lang)) {
      this.removeLanguage(lang);
      return;
    }
    this.selectedLanguages.push(lang);
    this.emitSelection();
    this.searchTerm = '';
  }

  removeLanguage(lang: Language) {
    this.selectedLanguages = this.selectedLanguages.filter(
      (l) => l.code !== lang.code
    );
    this.emitSelection();
  }

  isSelected(lang: Language): boolean {
    return this.selectedLanguages.some((l) => l.code === lang.code);
  }

  focusInput(input: HTMLInputElement) {
    input.focus();
  }

  private emitSelection() {
    this.selected.emit(this.selectedLanguages.map((l) => l.code));
  }

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }

  @HostListener('document:keydown.escape')
  handleEscapeKey() {
    this.showDropdown = false;
  }
}
