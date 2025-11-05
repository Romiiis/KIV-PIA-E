import { inject } from '@angular/core';
import {
  injectMutation,
  injectQueryClient,
} from '@tanstack/angular-query-experimental';
import { ProjectsWorkflowApiService } from '@api/apiServices/projects-workflow-api.service';
import { QK } from './query-keys';
import { toPromise } from './utils';

/**
 * Mutation: Upload přeloženého obsahu (TRANSLATOR).
 */
export function useUploadTranslatedMutation(id: string) {
  const api = inject(ProjectsWorkflowApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (file: File) => toPromise(api.uploadTranslated(id, file)),
    onSuccess: (updatedProject) => {
      qc.setQueryData(QK.project(id), updatedProject);
      qc.invalidateQueries({ queryKey: QK.projects });
    },
  }));
}

/**
 * Mutation: Schválení překladu (CUSTOMER).
 */
export function useApproveTranslatedMutation(id: string) {
  const api = inject(ProjectsWorkflowApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: () => toPromise(api.approveTranslated(id)),
    onSuccess: (updatedProject) => {
      qc.setQueryData(QK.project(id), updatedProject);
      qc.invalidateQueries({ queryKey: QK.projects });
    },
  }));
}

/**
 * Mutation: Zamítnutí překladu (CUSTOMER) s feedbackem.
 */
export function useRejectTranslatedMutation(id: string) {
  const api = inject(ProjectsWorkflowApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (feedback: string) => toPromise(api.rejectTranslated(id, feedback)),
    onSuccess: (updatedProject) => {
      qc.setQueryData(QK.project(id), updatedProject);
      qc.invalidateQueries({ queryKey: QK.projects });
    },
  }));
}

/**
 * Mutation: Uzavření projektu (ADMIN).
 */
export function useCloseProjectMutation(id: string) {
  const api = inject(ProjectsWorkflowApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: () => toPromise(api.close(id)),
    onSuccess: (closedProject) => {
      qc.setQueryData(QK.project(id), closedProject);
      qc.invalidateQueries({ queryKey: QK.projects });
    },
  }));
}
