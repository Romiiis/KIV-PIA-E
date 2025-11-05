import { inject } from '@angular/core';
import {
  injectQuery,
  injectMutation,
  injectQueryClient,
} from '@tanstack/angular-query-experimental';
import { ProjectsApiService } from '@api/apiServices/projects-api.service';
import { QK } from './query-keys';
import { toPromise } from './utils';
import { ProjectDomain } from '@core/models/project.model';

/**
 * Query: načte všechny projekty (podle role přihlášeného uživatele).
 */
export function useListProjectsQuery() {
  const api = inject(ProjectsApiService);
  return injectQuery(() => ({
    queryKey: QK.projects,
    queryFn: () => toPromise(api.listAll()),
    staleTime: 60_000, // 1 minuta – data se cachují, ale refetchují po čase
  }));
}

/**
 * Query: načte detail projektu podle ID.
 */
export function useGetProjectQuery(id: string) {
  const api = inject(ProjectsApiService);
  return injectQuery(() => ({
    queryKey: QK.project(id),
    queryFn: () => toPromise(api.detail(id)),
    enabled: !!id, // query se nespustí, dokud nemáš ID
  }));
}

/**
 * Mutation: vytvoření nového projektu (CUSTOMER).
 */
export function useCreateProjectMutation() {
  const api = inject(ProjectsApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (input: { languageCode: string; file: File }) =>
      toPromise(api.create(input.languageCode, input.file)),
    onSuccess: (newProject: ProjectDomain) => {
      // přidej nový projekt do cache (optimistická aktualizace)
      qc.setQueryData(QK.project(newProject.id), newProject);
      // invaliduj seznam, aby se refetchl s novým projektem
      qc.invalidateQueries({ queryKey: QK.projects });
    },
  }));
}

/**
 * Mutation: stáhne originální soubor projektu.
 */
export function useDownloadOriginalMutation(id: string) {
  const api = inject(ProjectsApiService);
  return injectMutation(() => ({
    mutationFn: () => toPromise(api.downloadOriginal(id)),
  }));
}

/**
 * Mutation: stáhne přeložený soubor projektu.
 */
export function useDownloadTranslatedMutation(id: string) {
  const api = inject(ProjectsApiService);
  return injectMutation(() => ({
    mutationFn: () => toPromise(api.downloadTranslated(id)),
  }));
}
