import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
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
  @Input() selectedValues: string[] | string | null = null;
  @Output() selected = new EventEmitter<string[] | string>();
  @ViewChild('inputEl') inputElement?: ElementRef<HTMLInputElement>;

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

      if (this.selectedValues) {
        if (this.multiple && Array.isArray(this.selectedValues)) {
          this.selectedLanguages = this.languages.filter((l) =>
            this.selectedValues?.includes(l.code)
          );
        } else if (!this.multiple && typeof this.selectedValues === 'string') {
          const found = this.languages.find(
            (l) => l.code === this.selectedValues
          );
          if (found) this.selectedLanguages = [found];
        }
      }
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
    if (this.multiple) {
      if (this.isSelected(lang)) {
        this.removeLanguage(lang);
      } else {
        this.selectedLanguages.push(lang);
      }
    } else {
      this.selectedLanguages = [lang];
      this.searchTerm = lang.name; // zobrazí vybraný jazyk v inputu
      this.showDropdown = false;
    }

    this.emitSelection();
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

  focusInput() {
    this.inputElement?.nativeElement.focus();
    this.filteredLanguages = this.languages; // zobrazí všechny
    this.showDropdown = true;
  }
  private emitSelection() {
    if (this.multiple) {
      this.selected.emit(this.selectedLanguages.map((l) => l.code));
    } else {
      this.selected.emit(this.selectedLanguages[0]?.code ?? '');
    }
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
