import { inject } from '@angular/core';
import { injectQuery } from '@tanstack/angular-query-experimental';
import { ProjectsFeedbackApiService } from '@api/apiServices/projects-feedback-api.service';
import { QK } from './query-keys';
import { toPromise } from './utils';
import { ProjectFeedback } from '@generated/models';

/**
 * Query: načte feedback konkrétního projektu.
 */
export function useProjectFeedbackQuery(projectId: string) {
  const api = inject(ProjectsFeedbackApiService);
  return injectQuery(() => ({
    queryKey: QK.projectFeedback(projectId),
    queryFn: () => toPromise(api.getFeedback(projectId)),
    enabled: !!projectId,
  }));
}
