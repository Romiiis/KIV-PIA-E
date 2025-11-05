import { inject } from '@angular/core';
import { injectMutation } from '@tanstack/angular-query-experimental';
import { ProjectsApiService } from '@api/apiServices/projects-api.service';
import { toPromise } from './utils';
import { ProjectDomain } from '@core/models/project.model';

/**
 * Creation mutation: Creation of a new project.
 * @return Mutation hook to create a new project.
 */
export function useCreateProjectMutation() {
  const api = inject(ProjectsApiService);

  return injectMutation(() => ({
    mutationFn: (input: { languageCode: string; file: File }) =>
      toPromise(api.create(input.languageCode, input.file)),
  }));
}

/**
 * Mutation: Get project details by ID.
 * @return Mutation hook to fetch project details.
 */
export function useGetProjectMutation() {
  const api = inject(ProjectsApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.detail(id)),
  }));
}


/**
 * Mutation: List all projects.
 * @return Mutation hook to list all projects.
 */
export function useListProjectsMutation() {
  const api = inject(ProjectsApiService);

  return injectMutation(() => ({
    mutationFn: () => toPromise(api.listAll()),
  }));
}

/**
 * Mutation: Download original file of the project.
 * @return Mutation hook to download the original file.
 */
export function useDownloadOriginalMutation() {
  const api = inject(ProjectsApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.downloadOriginal(id)),
  }));
}


/**
 * Mutation: Download translated file of the project.
 * @return Mutation hook to download the translated file.
 */
export function useDownloadTranslatedMutation() {
  const api = inject(ProjectsApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.downloadTranslated(id)),
  }));
}
