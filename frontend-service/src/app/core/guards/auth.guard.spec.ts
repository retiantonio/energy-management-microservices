import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { RoutePaths } from '../constants/route-paths';
import { AuthResponse } from '../models/auth.models';
import { SessionStore } from '../session/session.store';
import { adminGuard, authGuard, guestGuard } from './auth.guard';

describe('auth guards', () => {
  let router: Router;
  let sessionStore: SessionStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])],
    });

    router = TestBed.inject(Router);
    sessionStore = TestBed.inject(SessionStore);
  });

  function runGuard(guard: typeof authGuard | typeof guestGuard | typeof adminGuard) {
    const state = router.routerState.snapshot;
    return TestBed.runInInjectionContext(() => guard(state.root, state));
  }

  function setSession(role: AuthResponse['role']): void {
    sessionStore.setSession({
      token: 'test-token',
      username: 'johndoe',
      userId: '00000000-0000-0000-0000-000000000001',
      role,
      expiresInSeconds: 3600,
    });
  }

  function urlOf(result: ReturnType<typeof runGuard>): string | boolean {
    return result instanceof UrlTree ? result.toString() : (result as boolean);
  }

  it('should redirect unauthenticated visitors to login', () => {
    expect(urlOf(runGuard(authGuard))).toBe(`/${RoutePaths.login}`);
  });

  it('should redirect signed-in visitors away from login', () => {
    setSession('CLIENT');
    expect(urlOf(runGuard(guestGuard))).toBe(`/${RoutePaths.dashboard}`);
  });

  it('should allow guests to open login', () => {
    expect(runGuard(guestGuard)).toBe(true);
  });

  it('should restrict the users page to admins', () => {
    setSession('CLIENT');
    expect(urlOf(runGuard(adminGuard))).toBe(`/${RoutePaths.dashboard}`);
  });

  it('should allow admins into the users page', () => {
    setSession('ADMIN');
    expect(runGuard(adminGuard)).toBe(true);
  });
});
