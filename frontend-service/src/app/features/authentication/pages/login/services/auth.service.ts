import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest} from '../models/login.models';
import { RegisterRequest } from '../../register/models/register.models';

import { SessionStore } from '../../../../../core/session/session.store';
import { ApiEndpoints } from '../../../../../core/constants/api-endpoints';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sessionStore = inject(SessionStore);

  readonly authResponse = this.sessionStore.session;

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(ApiEndpoints.auth.login, request)
      .pipe(tap((response) => this.sessionStore.setSession(response)));
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(ApiEndpoints.auth.register, request)
      .pipe(tap((response) => this.sessionStore.setSession(response)));
  }

  logout(): void {
    this.sessionStore.clearSession();
  }
}
