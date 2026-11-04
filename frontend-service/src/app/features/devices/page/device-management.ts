import { Component, ElementRef, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { AuthService } from '../../authentication/pages/login/services/auth.service';
import { getHttpErrorDetails, HttpErrorDetails } from '../../../core/utils/http-error-message';
import { DeviceService } from '../services/device.service';
import { CreateDeviceRequest, Device } from '../models/device.models';
import { UserService } from '../../users/services/user.service';
import { User } from '../../users/models/user.models';
import { getDeviceOwnerLabel } from '../utils/device-owner-label';
import { DeviceManagementCard } from '../components/device-management-card/device-management-card';
import { filterDevices } from '../utils/filter-devices';
import { HttpErrorNotice } from '../../../core/components/http-error-notice/http-error-notice';

@Component({
  imports: [DeviceManagementCard, FormsModule, HttpErrorNotice],
  selector: 'app-device-management-page',
  styleUrl: './device-management.scss',
  templateUrl: './device-management.html',
})
export class DeviceManagementPage implements OnInit {
  @ViewChild('createDeviceDialog', { static: true })
  private readonly createDeviceDialog!: ElementRef<HTMLDialogElement>;

  private readonly authService = inject(AuthService);
  private readonly deviceService = inject(DeviceService);
  private readonly userService = inject(UserService);

  private readonly allDevices = signal<Device[]>([]);
  protected readonly devices = computed(() =>
    filterDevices(this.allDevices(), this.searchTerm(), (ownerUserId) =>
      this.ownerLabel(ownerUserId),
    ),
  );
  protected readonly users = signal<User[]>([]);
  protected readonly isAdmin = computed(() => this.authService.authResponse()?.role === 'ADMIN');
  protected readonly isLoadingDevices = signal(false);
  protected readonly isLoadingUsers = signal(false);
  protected readonly isSaving = signal(false);
  protected readonly isCreateFormOpen = signal(false);
  protected readonly editingDeviceId = signal<string | null>(null);
  protected readonly assigningDeviceId = signal<string | null>(null);
  protected readonly deviceError = signal<HttpErrorDetails | null>(null);
  protected readonly createDeviceError = signal<HttpErrorDetails | null>(null);
  protected readonly usersError = signal<HttpErrorDetails | null>(null);
  protected readonly successMessage = signal('');

  protected readonly searchTerm = signal('');
  protected assignmentUserId = '';
  protected readonly createForm = {
    name: '',
    description: '',
    maxEnergyConsumption: 1,
    ownerUserId: '',
  };
  protected readonly editForm = {
    name: '',
    description: '',
    maxEnergyConsumption: 1,
  };

  ngOnInit(): void {
    this.loadDevices();
    if (this.isAdmin()) {
      this.loadUsers();
    }
  }

  protected clearSearch(): void {
    this.searchTerm.set('');
  }

  protected openCreateForm(): void {
    if (!this.isAdmin() || this.isCreateFormOpen()) {
      return;
    }

    this.isCreateFormOpen.set(true);
    this.createDeviceError.set(null);
    this.deviceError.set(null);
    this.successMessage.set('');
    this.createDeviceDialog.nativeElement.showModal();
  }

  protected closeCreateForm(): void {
    if (this.createDeviceDialog.nativeElement.open) {
      this.createDeviceDialog.nativeElement.close();
    }
    this.isCreateFormOpen.set(false);
  }

  protected onCreateFormClosed(): void {
    this.isCreateFormOpen.set(false);
  }

  protected createDevice(): void {
    if (!this.isAdmin()) {
      return;
    }

    const request: CreateDeviceRequest = {
      name: this.createForm.name.trim(),
      description: this.createForm.description.trim() || null,
      maxEnergyConsumption: this.createForm.maxEnergyConsumption,
      ownerUserId: this.createForm.ownerUserId || null,
    };

    this.createDeviceError.set(null);
    this.mutate(
      this.deviceService.create(request),
      'Device created.',
      () => {
        this.closeCreateForm();
        this.createForm.name = '';
        this.createForm.description = '';
        this.createForm.maxEnergyConsumption = 1;
        this.createForm.ownerUserId = '';
      },
      (error) =>
        this.createDeviceError.set(getHttpErrorDetails(error, 'Could not create device.')),
    );
  }

  protected startEditing(device: Device): void {
    this.editingDeviceId.set(device.id);
    this.editForm.name = device.name;
    this.editForm.description = device.description ?? '';
    this.editForm.maxEnergyConsumption = device.maxEnergyConsumption;
    this.successMessage.set('');
  }

  protected cancelEditing(): void {
    this.editingDeviceId.set(null);
  }

  protected saveChanges(device: Device): void {
    if (!this.isAdmin()) {
      return;
    }

    const name = this.editForm.name.trim();
    if (!name) {
      return;
    }

    this.mutate(
      this.deviceService.update(device.id, {
        name,
        description: this.editForm.description.trim() || undefined,
        maxEnergyConsumption: this.editForm.maxEnergyConsumption,
      }),
      'Device updated.',
      () => this.editingDeviceId.set(null),
    );
  }

  protected deleteDevice(device: Device): void {
    if (!this.isAdmin() || !window.confirm(`Delete "${device.name}"? This cannot be undone.`)) {
      return;
    }

    this.mutate(this.deviceService.delete(device.id), 'Device deleted.');
  }

  protected toggleAssignment(device: Device): void {
    if (!this.isAdmin()) {
      return;
    }

    if (this.assigningDeviceId() === device.id) {
      this.assigningDeviceId.set(null);
      return;
    }

    this.assigningDeviceId.set(device.id);
    this.assignmentUserId = device.ownerUserId ?? '';
    this.successMessage.set('');
  }

  protected assignDevice(device: Device): void {
    if (!this.isAdmin() || !this.assignmentUserId) {
      return;
    }

    this.mutate(
      this.deviceService.assign(device.id, this.assignmentUserId),
      'Device assigned.',
      () => this.assigningDeviceId.set(null),
    );
  }

  protected unassignDevice(device: Device): void {
    if (!this.isAdmin()) {
      return;
    }

    this.mutate(this.deviceService.unassign(device.id), 'Device unassigned.');
  }

  protected ownerLabel(ownerUserId: string | null): string {
    return getDeviceOwnerLabel(ownerUserId, this.users(), this.isAdmin());
  }

  private loadDevices(): void {
    this.isLoadingDevices.set(true);
    this.deviceError.set(null);

    this.deviceService.list().subscribe({
      next: (devices) => {
        this.allDevices.set(devices);
        this.isLoadingDevices.set(false);
      },
      error: (error: unknown) => {
        this.isLoadingDevices.set(false);
        this.deviceError.set(getHttpErrorDetails(error, 'Could not load devices.'));
      },
    });
  }

  private loadUsers(): void {
    this.isLoadingUsers.set(true);
    this.usersError.set(null);

    this.userService.list().subscribe({
      next: (users) => {
        this.users.set(users);
        this.isLoadingUsers.set(false);
      },
      error: (error: unknown) => {
        this.isLoadingUsers.set(false);
        this.usersError.set(getHttpErrorDetails(error, 'Could not load users.'));
      },
    });
  }

  private mutate(
    operation: Observable<unknown>,
    successMessage: string,
    onSuccess?: () => void,
    onError?: (error: unknown) => void,
  ): void {
    if (!this.isAdmin() || this.isSaving()) {
      return;
    }

    this.isSaving.set(true);
    this.deviceError.set(null);
    this.successMessage.set('');

    operation.subscribe({
      next: () => {
        this.isSaving.set(false);
        this.successMessage.set(successMessage);
        onSuccess?.();
        this.loadDevices();
      },
      error: (error: unknown) => {
        this.isSaving.set(false);
        if (onError) {
          onError(error);
        } else {
          this.deviceError.set(getHttpErrorDetails(error, 'Device action failed.'));
        }
      },
    });
  }
}
