import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

/**
 * Interface for language objects.
 */
interface Language {
  code: string;
  name: string;
}

/**
 * This service loads and provides language data.
 * It uses Angular signals for reactive state management.
 * - Loads languages from '/languages.json' on startup.
 * - Provides a readonly signal for language selection components.
 * - Provides a fast lookup method for language names by code.
 */
@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private http = inject(HttpClient);

  private languagesSignal = signal<Language[]>([]);

  private languageMap = computed(() => {
    const map = new Map<string, string>();
    for (const lang of this.languagesSignal()) {
      map.set(lang.code, lang.name);
    }
    return map;
  });

  constructor() {
    this.http.get<Language[]>('/languages.json').pipe(
      tap(data => this.languagesSignal.set(data))
    ).subscribe();
  }

  /**
   * Returns a readonly signal of the languages array.
   * Used by 'language-select' component.
   * @returns Readonly signal of languages.
   */
  getLanguagesSignal() {
    return this.languagesSignal.asReadonly();
  }

  /**
   * Get the language name for a given code.
   * If the code is not found, returns the code itself.
   * @param code Language code.
   * @returns Language name or the code if not found.
   */
  getLanguageName(code: string): string {
    return this.languageMap().get(code) || code;
  }
}
