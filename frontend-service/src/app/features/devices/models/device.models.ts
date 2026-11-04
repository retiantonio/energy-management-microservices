export interface Device {
  id: string;
  name: string;
  description: string | null;
  maxEnergyConsumption: number;
  ownerUserId: string | null;
}

export interface CreateDeviceRequest {
  name: string;
  description: string | null;
  maxEnergyConsumption: number;
  ownerUserId: string | null;
}

export interface UpdateDeviceRequest {
  name: string;
  description?: string;
  maxEnergyConsumption: number;
}
