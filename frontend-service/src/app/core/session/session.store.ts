import { Injectable, signal } from '@angular/core';
import {
  AuthResponse,
  UserRole,
} from '../../features/authentication/pages/login/models/login.models';

const SESSION_STORAGE_KEY = 'service-portal.session';

interface StoredSession {
  session: AuthResponse;
  expiresAt: number;
}

function roleFromToken(token: string): UserRole | null {
  const parts = token.split('.');
  if (parts.length !== 3 || !/^[A-Za-z0-9_-]+$/.test(parts[1])) {
    return null;
  }

  try {
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '='))) as {
      role?: unknown;
    };

    return payload.role === 'ADMIN' || payload.role === 'CLIENT' ? payload.role : null;
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class SessionStore {
  readonly session = signal<AuthResponse | null>(this.restoreSession());

  setSession(session: AuthResponse): void {
    const role = roleFromToken(session.token);
    if (!role) {
      throw new TypeError('Authentication token does not contain a supported role.');
    }

    if (!Number.isFinite(session.expiresInSeconds) || session.expiresInSeconds <= 0) {
      throw new RangeError('Session expiration must be a positive finite number.');
    }

    const normalizedSession = { ...session, role };
    const storedSession: StoredSession = {
      session: normalizedSession,
      expiresAt: Date.now() + normalizedSession.expiresInSeconds * 1000,
    };
    if (!Number.isFinite(storedSession.expiresAt)) {
      throw new RangeError('Session expiration timestamp is outside the supported range.');
    }

    this.session.set(normalizedSession);

    try {
      globalThis.localStorage?.setItem(SESSION_STORAGE_KEY, JSON.stringify(storedSession));
    } catch (error) {
      console.error('Could not persist the session to localStorage.', error);
    }
  }

  clearSession(): void {
    this.session.set(null);

    try {
      globalThis.localStorage?.removeItem(SESSION_STORAGE_KEY);
    } catch (error) {
      console.error('Could not remove the session from localStorage.', error);
    }
  }

  private restoreSession(): AuthResponse | null {
    let storedValue: string | null;
    try {
      storedValue = globalThis.localStorage?.getItem(SESSION_STORAGE_KEY) ?? null;
    } catch (error) {
      console.error('Could not read the session from localStorage.', error);
      return null;
    }

    if (!storedValue) {
      return null;
    }

    let value: unknown;
    try {
      value = JSON.parse(storedValue);
    } catch (error) {
      console.warn('Discarding malformed session data from localStorage.', error);
      this.removeStoredSession();
      return null;
    }

    if (!this.isStoredSession(value)) {
      console.warn('Discarding invalid session data from localStorage.');
      this.removeStoredSession();
      return null;
    }

    if (value.expiresAt <= Date.now()) {
      this.removeStoredSession();
      return null;
    }

    const role = roleFromToken(value.session.token);
    if (!role) {
      console.warn('Discarding session with an unsupported token role.');
      this.removeStoredSession();
      return null;
    }

    return { ...value.session, role };
  }

  private isStoredSession(value: unknown): value is StoredSession {
    if (
      typeof value !== 'object' ||
      value === null ||
      !('session' in value) ||
      !('expiresAt' in value) ||
      typeof value.expiresAt !== 'number' ||
      !Number.isFinite(value.expiresAt)
    ) {
      return false;
    }

    const session = value.session;
    return (
      typeof session === 'object' &&
      session !== null &&
      'token' in session &&
      typeof session.token === 'string' &&
      session.token.length > 0 &&
      'username' in session &&
      typeof session.username === 'string' &&
      'userId' in session &&
      typeof session.userId === 'string' &&
      'role' in session &&
      (session.role === 'ADMIN' || session.role === 'CLIENT') &&
      'expiresInSeconds' in session &&
      typeof session.expiresInSeconds === 'number' &&
      Number.isFinite(session.expiresInSeconds) &&
      session.expiresInSeconds > 0
    );
  }

  private removeStoredSession(): void {
    try {
      globalThis.localStorage?.removeItem(SESSION_STORAGE_KEY);
    } catch (error) {
      console.error('Could not remove invalid session data from localStorage.', error);
    }
  }
}
