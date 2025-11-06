import {Component, ElementRef, forwardRef, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core'; // <-- Přidán ElementRef
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {MatAutocompleteModule, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatChipsModule} from '@angular/material/chips'; // <-- NOVÝ IMPORT
import {MatIconModule} from '@angular/material/icon'; // <-- NOVÝ IMPORT
import {Observable, of} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

interface Language {
  code: string;
  name: string;
}

@Component({
  selector: 'app-language-select',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule, // <-- PŘIDÁNO
    MatIconModule   // <-- PŘIDÁNO
  ],
  templateUrl: './language-select.component.html',
  styleUrls: ['./language-select.component.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LanguageSelectComponent),
      multi: true
    }
  ]
})
export class LanguageSelectComponent implements OnInit, ControlValueAccessor {

  // TODO - Pri multiselectu ukazovat nejnovejší výběr nahoře
  @Input() isMulti: boolean = false;
  @Input() placeholder: string = 'Select language...';

  searchControl = new FormControl('');
  allLanguages: Language[] = [];
  filteredLanguages$: Observable<Language[]> = of([]);
  selectedLanguages: Language[] = [];

  @ViewChild(MatAutocompleteTrigger) autocompleteTrigger!: MatAutocompleteTrigger;
  // Potřebujeme odkaz na input pro focus
  @ViewChild('languageInput') languageInput!: ElementRef<HTMLInputElement>;

  isFocused: boolean = false;

  onChange: (value: string[] | null) => void = () => {
  };
  onTouched: () => void = () => {
  };

  constructor(private http: HttpClient) {
  }


  ngOnInit(): void {
    this.http.get<Language[]>('/languages.json').subscribe(data => {
      this.allLanguages = data;
      this.filteredLanguages$ = this.searchControl.valueChanges.pipe(
        startWith(''),
        map(value => this._filter(value || ''))
      );
      const currentCodes = this.selectedLanguages.map(l => l.code);
      this._updateSelectionFromCodes(currentCodes);
    });
  }

  private _filter(value: string): Language[] {
    const filterValue = value.toLowerCase();
    return this.allLanguages.filter(lang =>
      (lang.name.toLowerCase().includes(filterValue) || lang.code.toLowerCase().includes(filterValue)) &&
      !this.selectedLanguages.some(l => l.code === lang.code)
    );
  }

  writeValue(codes: string[] | null): void {
    const validCodes = codes || [];
    if (this.allLanguages.length > 0) {
      this._updateSelectionFromCodes(validCodes);
    } else {
      this.selectedLanguages = validCodes.map(code => ({code: code, name: 'Načítá se...'}));
    }
  }

  private _updateSelectionFromCodes(codes: string[]) {
    this.selectedLanguages = this.allLanguages.filter(l => codes.includes(l.code));
    // Aktualizujeme text pouze v single-select módu
    if (!this.isMulti) {
      this.updateInputText();
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.searchControl.disable() : this.searchControl.enable();
  }

  isSelected(lang: Language): boolean {
    return this.selectedLanguages.some(l => l.code === lang.code);
  }

  // --- Tato metoda je NOVÁ ---
  /** Odebere jazyk (použito štítkem) */
  removeLanguage(lang: Language): void {
    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);
    if (index > -1) {
      this.selectedLanguages.splice(index, 1);
      this.onChange(this.selectedLanguages.map(l => l.code));
      this.onTouched();
      // Musíme znovu spustit filtrování, aby se odebraný jazyk vrátil do seznamu
      this.searchControl.updateValueAndValidity();
    }
  }

  // --- Tato metoda je UPRAVENA ---
  onToggleLanguage(event: MouseEvent, lang: Language): void {
    event.stopPropagation();
    event.preventDefault();

    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);

    if (this.isMulti) {
      if (index > -1) {
        // Již je vybrán, takže ho odebereme
        this.selectedLanguages.splice(index, 1);
      } else {
        // Není vybrán, přidáme ho
        // Add new selection at the beginning of the array
        this.selectedLanguages.unshift(lang);

        console.log('Adding language:', lang);
      }
      // Vyčistíme input a necháme panel otevřený
      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {
      // Single-select
      if (index > -1) {
        this.selectedLanguages = [];
      } else {
        this.selectedLanguages = [lang];
      }
      this.updateInputText(); // Zobrazí jméno v inputu
      this.autocompleteTrigger.closePanel();
    }

    this.onChange(this.selectedLanguages.map(l => l.code));
  }

  // Tato metoda se volá jen v single-select módu
  private updateInputText(): void {
    if (!this.isMulti) {
      const text = this.selectedLanguages[0]?.name || '';
      this.searchControl.setValue(text, {emitEvent: false});
    }
  }

  // --- Tato metoda je UPRAVENA ---
  onFocus(): void {
    // V single-select módu vyčistíme input pro hledání
    if (!this.isMulti) {
      this.searchControl.setValue('');
    }
  }

  // --- Tato metoda je UPRAVENA ---
  onBlur(): void {
    this.onTouched();
    if (this.isMulti) {
      // V multi-select módu jen vyčistíme input
      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {
      // V single-select módu obnovíme jméno vybraného jazyka
      this.updateInputText();
    }
  }

  displayFn(): string {
    return ''; // Vždy vracíme prázdný string, text ovládáme ručně
  }
}
