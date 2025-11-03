// api/queries/feedback.query.ts
import { inject } from '@angular/core';
import { injectQuery } from '@tanstack/angular-query-experimental';
import {ProjectFeedbackApiService} from '@api/apiServices/project-feedback-api.service';
import {queryFromApi} from '@api/query.utils';


export function useProjectFeedbackQuery(projectId: string) {
  const feedbackApi = inject(ProjectFeedbackApiService);
  return injectQuery(() => ({
    queryKey: ['feedback', projectId],
    queryFn: () => queryFromApi(feedbackApi.get(projectId)),
  }));
}
