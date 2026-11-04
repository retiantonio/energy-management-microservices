# Frontend service

Angular single-page application for the service portal. It provides login and
registration, a role-aware dashboard, device management, and an admin-only users
area.

## Project structure

```text
src/app/
  core/                         Shared auth/session infrastructure, routing guards,
                                HTTP interceptor, models, and route constants
  features/
    authentication/
      pages/{login,register}/   Auth page components, templates, and tests
      shared/                  Auth-page styles and auth error formatting
    dashboard/
      layout/                  Signed-in navigation shell
      pages/                   Dashboard overview
    devices/                    Device models, service, and management page
    users/                      User models, service, and admin page
```

Each feature owns its pages and data access. Cross-feature authentication state
and HTTP behavior live under `core`.

## Routes

- `/login` and `/register` — guest-only authentication pages
- `/dashboard` — signed-in overview
- `/dashboard/devices` — role-aware device management
- `/dashboard/users` — admin-only user area

The app calls backend endpoints through same-origin paths such as `/auth`,
`/devices`, and `/users`. The Angular development server proxies `/auth` to
Traefik at `http://localhost:80`.

## Development

From this directory:

```bash
npm start
npm run build
npm test -- --watch=false
```

To build and serve the frontend through Traefik from the repository root:

```bash
docker compose up -d --build frontend-service
```
