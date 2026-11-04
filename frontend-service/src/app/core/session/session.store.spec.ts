import { TestBed } from '@angular/core/testing';
import { AuthResponse } from '../../features/authentication/pages/login/models/login.models';
import { SessionStore } from './session.store';

const SESSION_STORAGE_KEY = 'service-portal.session';

function tokenWithRole(role: 'ADMIN' | 'CLIENT'): string {
  const payload = btoa(JSON.stringify({ role }))
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
  return `header.${payload}.signature`;
}

describe('SessionStore', () => {
  const session: AuthResponse = {
    token: tokenWithRole('CLIENT'),
    username: 'johndoe',
    userId: '00000000-0000-0000-0000-000000000001',
    role: 'CLIENT',
    expiresInSeconds: 3600,
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
  });

  afterEach(() => {
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  it('persists the session with its absolute expiration time', () => {
    const store = TestBed.inject(SessionStore);

    store.setSession(session);

    const stored = JSON.parse(localStorage.getItem(SESSION_STORAGE_KEY)!);
    expect(stored.session).toEqual(session);
    expect(stored.expiresAt).toBeGreaterThan(Date.now());
    expect(store.session()).toEqual(session);
  });

  it('restores a valid unexpired session', () => {
    localStorage.setItem(
      SESSION_STORAGE_KEY,
      JSON.stringify({ session, expiresAt: Date.now() + 60_000 }),
    );

    expect(TestBed.inject(SessionStore).session()).toEqual(session);
  });

  it('uses the token role instead of a modified stored role', () => {
    localStorage.setItem(
      SESSION_STORAGE_KEY,
      JSON.stringify({
        session: { ...session, role: 'ADMIN' },
        expiresAt: Date.now() + 60_000,
      }),
    );

    expect(TestBed.inject(SessionStore).session()?.role).toBe('CLIENT');
  });

  it('uses the token role instead of the API response role', () => {
    const store = TestBed.inject(SessionStore);

    store.setSession({ ...session, role: 'ADMIN' });

    expect(store.session()?.role).toBe('CLIENT');
  });

  it('discards expired session data', () => {
    localStorage.setItem(
      SESSION_STORAGE_KEY,
      JSON.stringify({ session, expiresAt: Date.now() - 1 }),
    );

    expect(TestBed.inject(SessionStore).session()).toBeNull();
    expect(localStorage.getItem(SESSION_STORAGE_KEY)).toBeNull();
  });

  it('clears persisted session data on logout', () => {
    const store = TestBed.inject(SessionStore);
    store.setSession(session);

    store.clearSession();

    expect(store.session()).toBeNull();
    expect(localStorage.getItem(SESSION_STORAGE_KEY)).toBeNull();
  });
});
