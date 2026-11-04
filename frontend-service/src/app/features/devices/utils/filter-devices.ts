import { Device } from '../models/device.models';

export function filterDevices(
  devices: Device[],
  query: string,
  ownerLabel: (ownerUserId: string | null) => string,
): Device[] {
  const normalizedQuery = query.trim().toLocaleLowerCase();
  if (!normalizedQuery) {
    return devices;
  }

  return devices.filter((device) =>
    [
      device.name,
      device.description ?? '',
      String(device.maxEnergyConsumption),
      device.ownerUserId ?? '',
      ownerLabel(device.ownerUserId),
    ].some((value) => value.toLocaleLowerCase().includes(normalizedQuery)),
  );
}
