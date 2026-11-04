export const ApiEndpoints = {
  auth: {
    login: '/api/auth/login',
    register: '/api/auth/register',
  },
  users: '/api/users',
  devices: '/api/devices',
} as const;
