import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AuthResponse } from '../models/login.models';
import { RegisterRequest } from '../../register/models/register.models';

import { SessionStore } from '../../../../../core/session/session.store';
import { ApiEndpoints } from '../../../../../core/constants/api-endpoints';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let authService: AuthService;
  let httpTesting: HttpTestingController;

  function tokenWithRole(role: 'ADMIN' | 'CLIENT'): string {
    const payload = btoa(JSON.stringify({ role }))
      .replace(/=/g, '')
      .replace(/\+/g, '-')
      .replace(/\//g, '_');
    return `header.${payload}.signature`;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });

    authService = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should register and store the returned session in memory', () => {
    const request: RegisterRequest = {
      username: 'johndoe',
      password: 'strong-password',
      name: 'John Doe',
      email: 'john@example.com',
      address: '10 Main Street',
    };
    const response: AuthResponse = {
      token: tokenWithRole('CLIENT'),
      username: request.username,
      userId: '00000000-0000-0000-0000-000000000001',
      role: 'CLIENT',
      expiresInSeconds: 3600,
    };

    authService.register(request).subscribe();

    const httpRequest = httpTesting.expectOne(ApiEndpoints.auth.register);
    expect(httpRequest.request.method).toBe('POST');
    expect(httpRequest.request.body).toEqual(request);
    httpRequest.flush(response);

    expect(authService.authResponse()).toEqual(response);
    expect(TestBed.inject(SessionStore).session()).toEqual(response);
  });
});
