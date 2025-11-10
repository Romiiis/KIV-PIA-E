import {
  ChangeDetectorRef,
  Component, effect,
  ElementRef,
  forwardRef,
  Input,
  OnInit,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatAutocompleteModule, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { Observable, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import {LanguageListService} from '@core/services/languageList.service';
import {TranslatePipe} from '@ngx-translate/core';


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
    MatChipsModule,
    MatIconModule,
    TranslatePipe
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


  @Input() isMulti: boolean = false;


  @Input() placeholder: string = 'languageSelect.placeholder';


  searchControl = new FormControl('');


  allLanguages: Language[] = [];


  filteredLanguages$: Observable<Language[]> = of([]);


  selectedLanguages: Language[] = [];

  pendingCodes: string[] | null = null;


  @ViewChild(MatAutocompleteTrigger) autocompleteTrigger!: MatAutocompleteTrigger;


  @ViewChild('languageInput') languageInput!: ElementRef<HTMLInputElement>;


  isFocused: boolean = false;


  onChange: (value: string[] | null) => void = () => {};
  onTouched: () => void = () => {};


  constructor(private languageService: LanguageListService, private cdr: ChangeDetectorRef) {
    effect(() => {
      this.allLanguages = this.languageService.getLanguagesSignal()();

      this.filteredLanguages$ = this.searchControl.valueChanges.pipe(
        startWith(this.searchControl.value || ''),
        map(value => this._filter(value || ''))
      );
      const codesToUpdate = this.pendingCodes ?? this.selectedLanguages.map(l => l.code);

      if (this.allLanguages.length > 0 && codesToUpdate.length > 0) {
        this._updateSelectionFromCodes(codesToUpdate);
        this.pendingCodes = null;
        this.cdr.detectChanges();
      }
    });

  }

  ngOnInit(): void {
    this.filteredLanguages$ = this.searchControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value || ''))
    );
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
      this.pendingCodes = null;
    } else {
      this.pendingCodes = validCodes;
      this.selectedLanguages = [];
    }
  }


  private _updateSelectionFromCodes(codes: string[]) {
    this.selectedLanguages = this.allLanguages.filter(l => codes.includes(l.code));
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


  removeLanguage(lang: Language): void {
    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);
    if (index > -1) {
      this.selectedLanguages.splice(index, 1);
      this.onChange(this.selectedLanguages.map(l => l.code));
      this.onTouched();

      this.searchControl.updateValueAndValidity();
    }
  }


  onToggleLanguage(event: MouseEvent, lang: Language): void {
    event.stopPropagation();
    event.preventDefault();

    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);

    if (this.isMulti) {

      if (index > -1) {
        this.selectedLanguages.splice(index, 1);
      } else {
        this.selectedLanguages.unshift(lang);
      }

      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {

      if (index > -1) {
        this.selectedLanguages = [];
      } else {
        this.selectedLanguages = [lang];
      }





      this.isFocused = false;


      this.updateInputText();


      this.autocompleteTrigger.closePanel();


      this.onTouched();
    }


    this.onChange(this.selectedLanguages.map(l => l.code));
  }


  private updateInputText(): void {
    if (!this.isMulti) {
      const text = this.selectedLanguages[0]?.name || '';

      this.searchControl.setValue(text, { emitEvent: false });
    }
  }


  onFocus(): void {
    this.isFocused = true;

    if (!this.isMulti) {
      this.searchControl.setValue('');
    }
  }


  onBlur(): void {
    this.isFocused = false;
    this.onTouched();

    if (this.isMulti) {

      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {

      this.updateInputText();
    }
  }


  displayFn(): string {
    return '';
  }
}
