import { User } from '../models/user.models';

export function filterUsers(users: User[], query: string): User[] {
  const normalizedQuery = query.trim().toLocaleLowerCase();
  if (!normalizedQuery) {
    return users;
  }

  return users.filter((user) =>
    [user.name, user.email, user.address, user.role, user.uuid].some((value) =>
      value.toLocaleLowerCase().includes(normalizedQuery),
    ),
  );
}
