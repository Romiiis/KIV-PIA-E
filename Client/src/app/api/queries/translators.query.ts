import { inject } from '@angular/core';
import { injectMutation } from '@tanstack/angular-query-experimental';
import { TranslatorsLanguageApiService } from '@api/services/translators-language-api.service';
import { toPromise } from './utils';


/**
 * Mutation: Load translator languages (TRANSLATOR role).
 */
export function useTranslatorLanguagesMutation() {
  const api = inject(TranslatorsLanguageApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.list(id)),
  }));
}


/**
 * Mutation: Replace translator languages (TRANSLATOR role).
 */
export function useReplaceTranslatorLanguagesMutation() {
  const api = inject(TranslatorsLanguageApiService);

  return injectMutation(() => ({
    mutationFn: (params: { id: string; languages: string[] }) =>
      toPromise(api.replace(params.id, params.languages)),
  }));
}
