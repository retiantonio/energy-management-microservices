import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { ApiEndpoints } from '../constants/api-endpoints';
import { SessionStore } from '../session/session.store';

const protectedApiPaths = [ApiEndpoints.users, ApiEndpoints.devices];

function isProtectedApiRequest(url: string): boolean {
  return protectedApiPaths.some(
    (path) => url === path || url.startsWith(`${path}/`) || url.startsWith(`${path}?`),
  );
}

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  if (!isProtectedApiRequest(request.url)) {
    return next(request);
  }

  const token = inject(SessionStore).session()?.token;
  if (!token) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }),
  );
};
