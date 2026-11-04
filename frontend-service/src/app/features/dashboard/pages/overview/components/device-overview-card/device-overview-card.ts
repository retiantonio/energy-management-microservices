import { Component, Input } from '@angular/core';
import { Device } from '../../../../../devices/models/device.models';

@Component({
  selector: 'app-device-overview-card',
  styleUrl: './device-overview-card.scss',
  templateUrl: './device-overview-card.html',
})
export class DeviceOverviewCard {
  @Input({ required: true }) device!: Device;
  @Input({ required: true }) owner!: string;
}
