import { inject } from '@angular/core';
import { injectMutation } from '@tanstack/angular-query-experimental';
import { ProjectsWorkflowApiService } from '@api/apiServices/projects-workflow-api.service';
import { toPromise } from './utils';

/**
 * Mutation: Upload translated file (TRANSLATOR).
 * @return Mutation hook to upload a translated file for a project.
 */
export function useUploadTranslatedMutation() {
  const api = inject(ProjectsWorkflowApiService);

  return injectMutation(() => ({
    mutationFn: (params: { id: string; file: File }) =>
      toPromise(api.uploadTranslated(params.id, params.file)),
  }));
}

/**
 * Mutation: Approval of translation (CUSTOMER).
 */
export function useApproveTranslatedMutation() {
  const api = inject(ProjectsWorkflowApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.approveTranslated(id)),
  }));
}

/**
 * Mutation: Rejection of translation with feedback (CUSTOMER).
 * @return Mutation hook to reject a translated project with feedback.
 */
export function useRejectTranslatedMutation() {
  const api = inject(ProjectsWorkflowApiService);

  return injectMutation(() => ({
    mutationFn: (params: { id: string; feedback: string }) =>
      toPromise(api.rejectTranslated(params.id, params.feedback)),
  }));
}

/**
 * Mutation: Close project (ADMIN).
 * @return Mutation hook to close a project.
 */
export function useCloseProjectMutation() {
  const api = inject(ProjectsWorkflowApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.close(id)),
  }));
}
