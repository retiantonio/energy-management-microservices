import { User } from '../../users/models/user.models';

export function getDeviceOwnerLabel(
  ownerUserId: string | null,
  users: User[],
  isAdmin: boolean,
): string {
  if (!ownerUserId) {
    return 'Unassigned';
  }

  const owner = users.find((user) => user.uuid === ownerUserId);
  if (owner) {
    return `${owner.name} (${owner.email})`;
  }

  return isAdmin ? ownerUserId : 'Assigned to your account';
}
