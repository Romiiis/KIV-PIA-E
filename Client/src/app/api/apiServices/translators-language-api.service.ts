import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { BaseApiService } from '@api/apiServices/base-api.service';
import { getTranslatorsLanguage } from '@generated/translators-language/translators-language';
import { ListLanguagesResponse, ReplaceLanguagesRequest } from '@generated/models';

const { listUserLanguages, replaceUserLanguages } = getTranslatorsLanguage();

/**
 * Facade for translator language-related API calls.
 * Handles listing and replacing languages for translators.
 */
@Injectable({ providedIn: 'root' })
export class TranslatorsLanguageApiService extends BaseApiService {

  /**
   * List languages for a translator by ID.
   * @param id Translator ID.
   */
  list(id: string): Observable<string[]> {
    return this.wrapPromise(listUserLanguages(id)).pipe(
      map((response: ListLanguagesResponse) => response)
    );
  }

  /**
   * Replace languages for a translator by ID.
   * @param id Translator ID.
   * @param languages Array of language codes to set.
   */
  replace(id: string, languages: string[]): Observable<string[]> {
    const body: ReplaceLanguagesRequest = languages;
    return this.wrapPromise(replaceUserLanguages(id, body)).pipe(
      map((response: ListLanguagesResponse) => response)
    );
  }
}
