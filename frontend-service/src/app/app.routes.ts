import { Routes } from '@angular/router';
import { adminGuard, authGuard, guestGuard } from './core/guards/auth.guard';
import { RoutePaths } from './core/constants/route-paths';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },

  {
    path: RoutePaths.login,
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/authentication/pages/login/page/login').then((module) => module.LoginPage),
  },

  {
    path: RoutePaths.register,
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/authentication/pages/register/page/register').then(
        (module) => module.RegisterPage,
      ),
  },

  {
    path: RoutePaths.dashboard,
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/layout/dashboard-shell/dashboard-shell').then(
        (module) => module.DashboardShell,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () =>
          import('./features/dashboard/pages/overview/page/overview').then(
            (module) => module.DashboardOverview,
          ),
      },

      {
        path: RoutePaths.devices,
        loadComponent: () =>
          import('./features/devices/page/device-management').then(
            (module) => module.DeviceManagementPage,
          ),
      },

      {
        path: RoutePaths.users,
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/users/page/user-management').then(
            (module) => module.UserManagementPage,
          ),
      },
    ],
  },

  {
    path: '**',
    redirectTo: 'login',
  },
];
