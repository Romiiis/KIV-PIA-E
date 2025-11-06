import { inject } from '@angular/core';
import { injectMutation } from '@tanstack/angular-query-experimental';
import { ProjectsFeedbackApiService } from '@api/services/projects-feedback-api.service';
import { toPromise } from './utils';
import { ProjectFeedback } from '@generated/models';


/**
 * Get feedback for a project by its projectId.
 * @return Mutation hook to fetch project feedback.
 */
export function useProjectFeedbackMutation() {
  const api = inject(ProjectsFeedbackApiService);

  return injectMutation<ProjectFeedback, Error, string>(() => ({
    mutationFn: (projectId: string) => toPromise(api.getFeedback(projectId)),
  }));
}
