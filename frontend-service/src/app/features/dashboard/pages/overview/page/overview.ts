import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../authentication/pages/login/services/auth.service';
import { DeviceService } from '../../../../devices/services/device.service';
import { Device } from '../../../../devices/models/device.models';
import { getDeviceOwnerLabel } from '../../../../devices/utils/device-owner-label';
import { filterDevices } from '../../../../devices/utils/filter-devices';
import { UserService } from '../../../../users/services/user.service';
import { User } from '../../../../users/models/user.models';
import { filterUsers } from '../../../../users/utils/filter-users';
import { HttpErrorDetails, getHttpErrorDetails } from '../../../../../core/utils/http-error-message';
import { DeviceOverviewCard } from '../components/device-overview-card/device-overview-card';
import { UserOverviewCard } from '../components/user-overview-card/user-overview-card';
import { HttpErrorNotice } from '../../../../../core/components/http-error-notice/http-error-notice';

@Component({
  imports: [DeviceOverviewCard, FormsModule, HttpErrorNotice, RouterLink, UserOverviewCard],
  selector: 'app-dashboard-overview',
  styleUrl: './overview.scss',
  templateUrl: './overview.html',
})
export class DashboardOverview implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly deviceService = inject(DeviceService);
  private readonly userService = inject(UserService);

  protected readonly session = this.authService.authResponse;
  protected readonly isAdmin = computed(() => this.session()?.role === 'ADMIN');
  private readonly allDevices = signal<Device[]>([]);
  protected readonly devices = computed(() =>
    filterDevices(this.allDevices(), this.searchTerm(), (ownerUserId) =>
      this.ownerLabel(ownerUserId),
    ),
  );
  private readonly allUsers = signal<User[]>([]);
  protected readonly users = computed(() => filterUsers(this.allUsers(), this.usersSearchTerm()));
  protected readonly isLoadingDevices = signal(false);
  protected readonly isLoadingUsers = signal(false);
  protected readonly deviceError = signal<HttpErrorDetails | null>(null);
  protected readonly usersError = signal<HttpErrorDetails | null>(null);
  protected readonly searchTerm = signal('');
  protected readonly usersSearchTerm = signal('');

  ngOnInit(): void {
    this.loadDevices();
    if (this.isAdmin()) {
      this.loadUsers();
    }
  }

  protected clearSearch(): void {
    this.searchTerm.set('');
  }

  protected clearUsersSearch(): void {
    this.usersSearchTerm.set('');
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
        this.allUsers.set(users);
        this.isLoadingUsers.set(false);
      },
      error: (error: unknown) => {
        this.isLoadingUsers.set(false);
        this.usersError.set(getHttpErrorDetails(error, 'Could not load users.'));
      },
    });
  }
}
