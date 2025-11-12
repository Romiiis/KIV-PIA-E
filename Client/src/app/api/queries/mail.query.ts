import {inject} from '@angular/core';
import {injectMutation, injectQuery, injectQueryClient} from '@tanstack/angular-query-experimental';
import {toPromise} from '@api/queries/utils';
import {MailApiService} from '@api/services/mail-api.service';

/**
 * Mutation: logout user.
 * @return Mutation for logging out.
 */
export function useMail() {
  const api = inject(MailApiService);

  return injectMutation(() => ({
    mutationFn: (input: {projectId: string, sendCustomer: boolean, sendTranslator: boolean, textToSend: string}) => toPromise(api.sendMail(input))
  }));
}
