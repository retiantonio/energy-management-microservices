import { UserRole } from '../../authentication/pages/login/models/login.models';

export interface User {
  uuid: string;
  name: string;
  email: string;
  address: string;
  role: UserRole;
}
