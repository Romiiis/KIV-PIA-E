import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BaseApiService} from './base-api.service';
import {getMails} from '@generated/mails/mails';

const {
  sendEmail
} = getMails();


/**
 * Facade for mail-related API calls.
 *
 */
@Injectable({providedIn: 'root'})
export class MailApiService extends BaseApiService {

  /**
   * Log in user with email and password.
   * On success, calls /me and returns domain model UserDomain.
   * */
  sendMail(input: {
    projectId: string;
    sendCustomer: boolean;
    sendTranslator: boolean;
    textToSend: string;
  }): Observable<void> {
    return this.wrapPromise(sendEmail({
        projectId: input.projectId,
        customer: input.sendCustomer,
        translator: input.sendTranslator,
        text: input.textToSend
      }
    )).pipe(map(() => void 0));
  }


}
