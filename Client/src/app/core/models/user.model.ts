export interface User {
  id: string;
  name: string;
  emailAddress: string;
  role: 'customer' | 'translator' | 'admin';
}
