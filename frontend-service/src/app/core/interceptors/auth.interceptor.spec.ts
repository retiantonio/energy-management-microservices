import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiEndpoints } from '../constants/api-endpoints';
import { SessionStore } from '../session/session.store';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let sessionStore: SessionStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    sessionStore = TestBed.inject(SessionStore);
    sessionStore.clearSession();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should attach the in-memory token only to protected API requests', () => {
    const tokenPayload = btoa(JSON.stringify({ role: 'CLIENT' })).replace(/=/g, '');
    const token = `header.${tokenPayload}.signature`;
    sessionStore.setSession({
      token,
      username: 'johndoe',
      userId: '00000000-0000-0000-0000-000000000001',
      role: 'CLIENT',
      expiresInSeconds: 3600,
    });

    http.get(`${ApiEndpoints.devices}?name=lamp`).subscribe();

    const request = httpTesting.expectOne(`${ApiEndpoints.devices}?name=lamp`);
    expect(request.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    request.flush([]);

    http.post(ApiEndpoints.auth.login, {}).subscribe();
    const login = httpTesting.expectOne(ApiEndpoints.auth.login);
    expect(login.request.headers.has('Authorization')).toBe(false);
    login.flush({});
  });

  it('should leave protected requests unchanged when signed out', () => {
    http.get(ApiEndpoints.users).subscribe();

    const request = httpTesting.expectOne(ApiEndpoints.users);
    expect(request.request.headers.has('Authorization')).toBe(false);
    request.flush([]);
  });
});
