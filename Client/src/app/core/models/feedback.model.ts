import { Expose } from 'class-transformer';

/**
 * Domain model for user feedback.
 */
export class FeedbackDomain {
  @Expose()
  projectId!: string;

  @Expose()
  text!: string;

  @Expose()
  createdAt!: string;
}

