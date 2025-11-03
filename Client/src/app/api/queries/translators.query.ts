import { inject } from '@angular/core';
import { injectQuery, injectMutation, injectQueryClient } from '@tanstack/angular-query-experimental';
import type { ReplaceLanguagesRequest, ListLanguagesResponse } from '@generated/model';
import {TranslatorsApiService} from '@api/apiServices/translators-api.service';
import {queryFromApi} from '@api/query.utils';

/**
 * Fetch languages known by a specific user
 * (ADMIN or translator themselves)
 */
export function useUserLanguagesQuery(userId: string) {
  const translatorsApi = inject(TranslatorsApiService);

  return injectQuery(() => ({
    queryKey: ['user', userId, 'languages'],
    queryFn: () => queryFromApi(translatorsApi.getUserLanguages(userId)),
    staleTime: 1000 * 60 * 5, // cache 5 minut
    refetchOnWindowFocus: true,
  }));
}

/**
 * Replace all languages of a translator
 * (only the translator themselves)
 */
export function useReplaceUserLanguagesMutation() {
  const translatorsApi = inject(TranslatorsApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (input: { userId: string; payload: ReplaceLanguagesRequest }) =>
      queryFromApi(translatorsApi.replaceUserLanguages(input.userId, input.payload)),

    // Po úspěchu refreshneme jazykovou cache konkrétního uživatele
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['user', variables.userId, 'languages'] });
    },
  }));
}
