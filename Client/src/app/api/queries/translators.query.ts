import { inject } from '@angular/core';
import {
  injectQuery,
  injectMutation,
  injectQueryClient,
} from '@tanstack/angular-query-experimental';
import { TranslatorsLanguageApiService } from '@api/apiServices/translators-language-api.service';
import { QK } from './query-keys';
import { toPromise } from './utils';

/**
 * Query: načtení jazyků daného uživatele (translator nebo admin).
 */
export function useTranslatorLanguagesQuery(id: string) {
  const api = inject(TranslatorsLanguageApiService);
  return injectQuery(() => ({
    queryKey: QK.userLanguages(id),
    queryFn: () => toPromise(api.list(id)),
    enabled: !!id,
  }));
}

/**
 * Mutation: náhrada jazyků překladatele (TRANSLATOR role).
 */
export function useReplaceTranslatorLanguagesMutation(id: string) {
  const api = inject(TranslatorsLanguageApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (languages: string[]) => toPromise(api.replace(id, languages)),
    onSuccess: (updatedLanguages) => {
      // aktualizace cache jazyků a invalidace detailu uživatele
      qc.setQueryData(QK.userLanguages(id), updatedLanguages);
      qc.invalidateQueries({ queryKey: QK.user(id) });
    },
  }));
}
