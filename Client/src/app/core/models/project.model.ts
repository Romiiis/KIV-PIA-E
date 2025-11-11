import {Expose, Type} from 'class-transformer';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {UserDomain} from '@core/models/user.model';
import {FeedbackDomain} from '@core/models/feedback.model';

/**
 * Domain model for a translation project.
 */
export class ProjectDomain {
  @Expose()
  id!: string;

  @Expose()
  @Type(() => UserDomain)
  customer!: UserDomain;

  @Expose()
  @Type(() => UserDomain)
  translator!: UserDomain | null;

  @Expose()
  originalFileName!: string;

  @Expose()
  translatedFileName!: string | null;

  @Expose()
  targetLanguage!: string;

  @Expose({name: 'state'})
  status!: ProjectStatusDomain;

  @Expose()
  createdAt!: string;

  @Expose()
  feedback!: FeedbackDomain;

  /**
   * Returns a modified status class for UI representation.
   * @returns A string representing the status class.
   */
  public getStatusClass(): string {

    if (this.status === 'ASSIGNED' && (this.feedback !== undefined && this.feedback !== null)) {
      return 'REWORK';
    }

    if (this.status === 'CLOSED' && !this.translator) {
      return 'CANCELED';
    }

    return this.status;
  }


}
