// api/queries/projects.query.ts
import { inject, Injectable } from '@angular/core';
import { injectQuery, injectMutation, injectQueryClient } from '@tanstack/angular-query-experimental';
import type { CreateProjectRequest, ListAllProjectsParams, Project } from '@generated/model';
import {queryFromApi} from '@api/query.utils';
import {ProjectsApiService} from '@api/apiServices/projects-api.service';

export function useProjectsQuery(params?: ListAllProjectsParams) {
  const projectsApi = inject(ProjectsApiService);
  return injectQuery(() => ({
    queryKey: ['projects', params],
    queryFn: () => queryFromApi(projectsApi.getAll(params)),
  }));
}

export function useProjectQuery(id: string) {
  const projectsApi = inject(ProjectsApiService);
  return injectQuery(() => ({
    queryKey: ['project', id],
    queryFn: () => queryFromApi(projectsApi.getById(id)),
  }));
}

export function useCreateProjectMutation() {
  const projectsApi = inject(ProjectsApiService);
  const queryClient = injectQueryClient();
  return injectMutation(() => ({
    mutationFn: (payload: CreateProjectRequest) => queryFromApi(projectsApi.create(payload)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  }));
}
