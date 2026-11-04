import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Device } from '../../models/device.models';

@Component({
  selector: 'app-device-management-card',
  styleUrl: './device-management-card.scss',
  templateUrl: './device-management-card.html',
})
export class DeviceManagementCard {
  @Input({ required: true }) device!: Device;
  @Input({ required: true }) owner!: string;
  @Input() isAdmin = false;
  @Input() isSaving = false;

  @Output() edit = new EventEmitter<void>();
  @Output() assign = new EventEmitter<void>();
  @Output() unassign = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
}
