import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoutePaths } from '../constants/route-paths';
import { SessionStore } from '../session/session.store';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  return inject(SessionStore).session() ? true : router.parseUrl(`/${RoutePaths.login}`);
};

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);
  return inject(SessionStore).session() ? router.parseUrl(`/${RoutePaths.dashboard}`) : true;
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const session = inject(SessionStore).session();

  if (!session) {
    return router.parseUrl(`/${RoutePaths.login}`);
  }

  return session.role === 'ADMIN' ? true : router.parseUrl(`/${RoutePaths.dashboard}`);
};
