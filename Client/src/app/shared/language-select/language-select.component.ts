import {
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
import {LanguageService} from '@core/services/language.service';

/**
 * Interface representing a single language entity.
 */
interface Language {
  code: string;
  name: string;
}

/**
 * A highly-customizable language select component that supports both
 * single-select (with a custom "dropdown" look) and multi-select (with chips) modes.
 *
 * It implements ControlValueAccessor to integrate with Angular Forms.
 */
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
    MatIconModule
  ],
  templateUrl: './language-select.component.html',
  styleUrls: ['./language-select.component.css'],
  encapsulation: ViewEncapsulation.None, // Required for custom autocomplete panel styles
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LanguageSelectComponent),
      multi: true
    }
  ]
})
export class LanguageSelectComponent implements OnInit, ControlValueAccessor {

  /**
   * Toggles the component between multi-select (chips) and single-select (dropdown) modes.
   */
  @Input() isMulti: boolean = false;

  /**
   * Placeholder text to display in the input.
   */
  @Input() placeholder: string = 'Select language...';

  /** The FormControl used to manage the text input for searching. */
  searchControl = new FormControl('');

  /** Stores the complete list of languages fetched from the server. */
  allLanguages: Language[] = [];

  /** An observable stream of languages, filtered by the user's search input. */
  filteredLanguages$: Observable<Language[]> = of([]);

  /** The list of currently selected `Language` objects. */
  selectedLanguages: Language[] = [];

  /** Reference to the autocomplete trigger to manually control the panel. */
  @ViewChild(MatAutocompleteTrigger) autocompleteTrigger!: MatAutocompleteTrigger;

  /** Reference to the native `<input>` element. */
  @ViewChild('languageInput') languageInput!: ElementRef<HTMLInputElement>;

  /**
   * Tracks the focus state of the component.
   * This is crucial for the single-select UI to toggle between the
   * static placeholder (`<span>`) and the active search (`<input>`).
   */
  isFocused: boolean = false;

  // --- ControlValueAccessor Functions ---
  onChange: (value: string[] | null) => void = () => {};
  onTouched: () => void = () => {};
  // ------------------------------------

  constructor(private languageService: LanguageService) {
    effect(() => {
      this.allLanguages = this.languageService.getLanguagesSignal()();

      this.filteredLanguages$ = this.searchControl.valueChanges.pipe(
        startWith(this.searchControl.value || ''),
        map(value => this._filter(value || ''))
      );
      const currentCodes = this.selectedLanguages.map(l => l.code);
      this._updateSelectionFromCodes(currentCodes);
    });
  }

  ngOnInit(): void {
    this.filteredLanguages$ = this.searchControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value || ''))
    );
  }

  /**
   * Filters the `allLanguages` list based on the user's search term.
   * It also hides languages that are already selected (in multi-select mode).
   * @param value The search string.
   * @returns A filtered array of `Language` objects.
   */
  private _filter(value: string): Language[] {
    const filterValue = value.toLowerCase();
    return this.allLanguages.filter(lang =>
      (lang.name.toLowerCase().includes(filterValue) || lang.code.toLowerCase().includes(filterValue)) &&
      !this.selectedLanguages.some(l => l.code === lang.code) // Hide already selected
    );
  }

  // ===================================================================
  //  ControlValueAccessor Implementation
  // ===================================================================

  /**
   * Writes a new value (an array of language codes) to the component.
   * This method handles the value being set from the parent form.
   * @param codes An array of language codes (e.g., ['en', 'fr']).
   */
  writeValue(codes: string[] | null): void {
    const validCodes = codes || [];
    if (this.allLanguages.length > 0) {
      // If languages are loaded, we can find the full objects
      this._updateSelectionFromCodes(validCodes);
    } else {
      // If languages aren't loaded yet, store partial objects.
      // `ngOnInit` will fix them later.
      this.selectedLanguages = validCodes.map(code => ({ code: code, name: '' }) as Language);
    }
  }

  /**
   * Updates the `selectedLanguages` array based on a list of codes.
   * @param codes An array of language codes.
   */
  private _updateSelectionFromCodes(codes: string[]) {
    this.selectedLanguages = this.allLanguages.filter(l => codes.includes(l.code));
    // Update the visual text only in single-select mode
    if (!this.isMulti) {
      this.updateInputText();
    }
  }

  /**
   * Registers a callback function to be called when the component's value changes.
   * @param fn The callback function.
   */
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  /**
   * Registers a callback function to be called when the component is "touched".
   * @param fn The callback function.
   */
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  /**
   * Sets the disabled state of the component.
   * @param isDisabled Whether the component should be disabled.
   */
  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.searchControl.disable() : this.searchControl.enable();
  }

  // ===================================================================
  //  Component-Specific Logic & UI Handlers
  // ===================================================================

  /**
   * Helper function to check if a language is currently selected.
   * @param lang The `Language` object to check.
   * @returns `true` if the language is selected, `false` otherwise.
   */
  isSelected(lang: Language): boolean {
    return this.selectedLanguages.some(l => l.code === lang.code);
  }

  /**
   * Removes a language from the selection (used by chips in multi-select mode).
   * @param lang The `Language` object to remove.
   */
  removeLanguage(lang: Language): void {
    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);
    if (index > -1) {
      this.selectedLanguages.splice(index, 1);
      this.onChange(this.selectedLanguages.map(l => l.code));
      this.onTouched();
      // Refresh the autocomplete filter
      this.searchControl.updateValueAndValidity();
    }
  }

  /**
   * Handles the selection/deselection of a language from the autocomplete panel.
   * This is the core UI logic for both single and multi-select modes.
   * @param event The mouse event (to prevent default behavior).
   * @param lang The `Language` object being toggled.
   */
  onToggleLanguage(event: MouseEvent, lang: Language): void {
    event.stopPropagation();
    event.preventDefault();

    const index = this.selectedLanguages.findIndex(l => l.code === lang.code);

    if (this.isMulti) {
      // --- Multi-select Logic (Chips) ---
      if (index > -1) {
        this.selectedLanguages.splice(index, 1);
      } else {
        this.selectedLanguages.unshift(lang); // Add to the beginning
      }
      // Clear the search input
      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {
      // --- Single-select Logic (Custom Dropdown) ---
      if (index > -1) {
        this.selectedLanguages = []; // Deselect if clicking the same one
      } else {
        this.selectedLanguages = [lang];
      }

      // We manually manage the state transition here instead of relying on blur().
      // This provides an immediate visual update when an option is clicked.

      // 1. Force the component into its "unfocused" visual state
      this.isFocused = false;

      // 2. Update the hidden searchControl value (which also updates the static <span>)
      this.updateInputText();

      // 3. Manually close the autocomplete panel
      this.autocompleteTrigger.closePanel();

      // 4. Mark the component as "touched"
      this.onTouched();
    }

    // Notify the parent form of the value change
    this.onChange(this.selectedLanguages.map(l => l.code));
  }

  /**
   * Updates the `searchControl` value with the selected language's name.
   * This is ONLY used in single-select mode to display the final value.
   */
  private updateInputText(): void {
    if (!this.isMulti) {
      const text = this.selectedLanguages[0]?.name || '';
      // Set value without emitting a change event, as it's not a user search
      this.searchControl.setValue(text, { emitEvent: false });
    }
  }

  /**
   * Handles the `focus` event on the input.
   * Sets the `isFocused` flag and clears the input for searching.
   */
  onFocus(): void {
    this.isFocused = true;
    // In single-select mode, clear the displayed name to allow searching
    if (!this.isMulti) {
      this.searchControl.setValue('');
    }
  }

  /**
   * Handles the `blur` event on the input.
   * Resets the visual state of the component.
   */
  onBlur(): void {
    this.isFocused = false;
    this.onTouched();

    if (this.isMulti) {
      // Always clear the search input on blur in multi-mode
      this.searchControl.setValue('');
      if (this.languageInput) {
        this.languageInput.nativeElement.value = '';
      }
    } else {
      // In single-mode, restore the selected language name
      this.updateInputText();
    }
  }

  /**
   * `displayWith` function for `matAutocomplete`.
   * We return an empty string because we manually control the input's text value
   * via `isFocused` and `updateInputText()`.
   */
  displayFn(): string {
    return '';
  }
}
