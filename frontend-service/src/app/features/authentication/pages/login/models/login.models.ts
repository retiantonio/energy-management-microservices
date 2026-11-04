export interface LoginRequest {
  username: string;
  password: string;
}

export type UserRole = 'CLIENT' | 'ADMIN';

export interface AuthResponse {
  token: string;
  username: string;
  userId: string;
  role: UserRole;
  expiresInSeconds: number;
}