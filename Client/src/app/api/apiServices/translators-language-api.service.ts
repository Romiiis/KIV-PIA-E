import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { BaseApiService } from '@api/apiServices/base-api.service';
import { getTranslatorsLanguage } from '@generated/translators-language/translators-language';
import { ListLanguagesResponse, ReplaceLanguagesRequest } from '@generated/models';

const { listUserLanguages, replaceUserLanguages } = getTranslatorsLanguage();

/**
 * Fasáda pro správu jazyků překladatelů — načítání a nahrazování jazyků.
 */
@Injectable({ providedIn: 'root' })
export class TranslatorsLanguageApiService extends BaseApiService {
  /**
   * Načte seznam jazyků daného uživatele (ADMIN nebo překladatel sám).
   */
  list(id: string): Observable<string[]> {
    return this.wrapPromise(listUserLanguages(id)).pipe(
      map((response: ListLanguagesResponse) => response)
    );
  }

  /**
   * Nahradí všechny jazyky překladatele novými.
   */
  replace(id: string, languages: string[]): Observable<string[]> {
    const body: ReplaceLanguagesRequest = languages;
    return this.wrapPromise(replaceUserLanguages(id, body)).pipe(
      map((response: ListLanguagesResponse) => response)
    );
  }
}
