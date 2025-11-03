// api/queries/workflow.query.ts
import { inject } from '@angular/core';
import { injectMutation, injectQueryClient } from '@tanstack/angular-query-experimental';
import type { ProjectFeedbackRequest, UploadTranslatedContentBody } from '@generated/model';
import {ProjectWorkflowApiService} from '@api/apiServices/project-workflow-api.service';
import {queryFromApi} from '@api/query.utils';

export function useUploadTranslationMutation() {
  const workflowApi = inject(ProjectWorkflowApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (input: { projectId: string; payload: UploadTranslatedContentBody }) =>
      queryFromApi(workflowApi.uploadTranslatedContent(input.projectId, input.payload)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  }));
}

export function useApproveTranslationMutation() {
  const workflowApi = inject(ProjectWorkflowApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (projectId: string) => queryFromApi(workflowApi.approve(projectId)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  }));
}

export function useRejectTranslationMutation() {
  const workflowApi = inject(ProjectWorkflowApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (input: { projectId: string; feedback: ProjectFeedbackRequest }) =>
      queryFromApi(workflowApi.reject(input.projectId, input.feedback)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  }));
}

export function useCloseProjectMutation() {
  const workflowApi = inject(ProjectWorkflowApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (projectId: string) => queryFromApi(workflowApi.close(projectId)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  }));
}
