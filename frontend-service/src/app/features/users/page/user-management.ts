import { Component, ElementRef, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { getHttpErrorDetails, HttpErrorDetails } from '../../../core/utils/http-error-message';
import { AuthService } from '../../authentication/pages/login/services/auth.service';
import { User } from '../models/user.models';
import { CreateUserRequest, UserService } from '../services/user.service';
import { UserManagementCard } from '../components/user-management-card/user-management-card';
import { filterUsers } from '../utils/filter-users';
import { HttpErrorNotice } from '../../../core/components/http-error-notice/http-error-notice';

@Component({
  imports: [FormsModule, HttpErrorNotice, UserManagementCard],
  selector: 'app-user-management-page',
  styleUrl: './user-management.scss',
  templateUrl: './user-management.html',
})
export class UserManagementPage implements OnInit {
  @ViewChild('createUserDialog', { static: true })
  private readonly createUserDialog!: ElementRef<HTMLDialogElement>;

  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);

  private readonly allUsers = signal<User[]>([]);
  protected readonly users = computed(() => filterUsers(this.allUsers(), this.searchTerm()));
  protected readonly isLoading = signal(false);
  protected readonly isSaving = signal(false);
  protected readonly isCreateFormOpen = signal(false);
  protected readonly editingUserId = signal<string | null>(null);
  protected readonly errorDetails = signal<HttpErrorDetails | null>(null);
  protected readonly createUserError = signal<HttpErrorDetails | null>(null);
  protected readonly editUserError = signal<HttpErrorDetails | null>(null);
  protected readonly successMessage = signal('');
  protected readonly createPasswordVisible = signal(false);
  protected readonly editPasswordVisible = signal(false);

  protected readonly searchTerm = signal('');
  protected readonly createForm: CreateUserRequest = {
    role: 'CLIENT',
    username: '',
    password: '',
    name: '',
    email: '',
    address: '',
  };
  
  protected readonly editForm = {
    username: '',
    password: '',
    name: '',
    email: '',
    address: '',
  };

  ngOnInit(): void {
    this.loadUsers();
  }

  protected clearSearch(): void {
    this.searchTerm.set('');
  }

  protected openCreateForm(): void {
    if (!this.isAdmin() || this.isCreateFormOpen()) {
      return;
    }

    this.isCreateFormOpen.set(true);
    this.errorDetails.set(null);
    this.createUserError.set(null);
    this.createPasswordVisible.set(false);
    this.successMessage.set('');
    this.createUserDialog.nativeElement.showModal();
  }

  protected closeCreateForm(): void {
    if (this.createUserDialog.nativeElement.open) {
      this.createUserDialog.nativeElement.close();
    }
    this.isCreateFormOpen.set(false);
    this.createPasswordVisible.set(false);
  }

  protected onCreateFormClosed(): void {
    this.isCreateFormOpen.set(false);
    this.createPasswordVisible.set(false);
  }

  protected createUser(): void {
    if (!this.isAdmin()) {
      return;
    }

    const request: CreateUserRequest = {
      ...this.createForm,
      username: this.createForm.username.trim(),
      name: this.createForm.name.trim(),
      email: this.createForm.email.trim(),
      address: this.createForm.address.trim(),
    };

    this.createUserError.set(null);
    this.mutate(
      this.userService.create(request),
      'User created.',
      () => {
        this.closeCreateForm();
        this.createForm.role = 'CLIENT';
        this.createForm.username = '';
        this.createForm.password = '';
        this.createPasswordVisible.set(false);
        this.createForm.name = '';
        this.createForm.email = '';
        this.createForm.address = '';
      },
      (error) => this.createUserError.set(getHttpErrorDetails(error, 'Could not create user.')),
    );
  }

  protected startEditing(user: User): void {
    this.editingUserId.set(user.uuid);
    this.editUserError.set(null);
    this.editPasswordVisible.set(false);
    this.editForm.username = '';
    this.editForm.password = '';
    this.editForm.name = user.name;
    this.editForm.email = user.email;
    this.editForm.address = user.address;
    this.errorDetails.set(null);
    this.successMessage.set('');
  }

  protected cancelEditing(): void {
    this.editingUserId.set(null);
    this.editUserError.set(null);
    this.editPasswordVisible.set(false);
  }

  protected saveChanges(user: User): void {
    if (!this.isAdmin()) {
      return;
    }

    this.editUserError.set(null);
    this.mutate(
      this.userService.update(user.uuid, {
        username: this.editForm.username.trim() || undefined,
        password: this.editForm.password || undefined,
        name: this.editForm.name.trim(),
        email: this.editForm.email.trim(),
        address: this.editForm.address.trim(),
      }),
      'User updated.',
      () => {
        this.editingUserId.set(null);
        this.editPasswordVisible.set(false);
      },
      (error) => this.editUserError.set(getHttpErrorDetails(error, 'Could not update user.')),
    );
  }

  protected deleteUser(user: User): void {
    if (
      !this.isAdmin() ||
      !window.confirm(
        `Delete "${user.name}"? This also removes their credentials and unassigns their devices.`,
      )
    ) {
      return;
    }

    this.mutate(this.userService.delete(user.uuid), 'User deleted.');
  }

  private isAdmin(): boolean {
    return this.authService.authResponse()?.role === 'ADMIN';
  }

  private loadUsers(): void {
    this.isLoading.set(true);
    this.errorDetails.set(null);

    this.userService.list().subscribe({
      next: (users) => {
        this.allUsers.set(users);
        this.isLoading.set(false);
      },
      error: (error: unknown) => {
        this.isLoading.set(false);
        this.errorDetails.set(getHttpErrorDetails(error, 'Could not load users.'));
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
    this.errorDetails.set(null);
    this.successMessage.set('');

    operation.subscribe({
      next: () => {
        this.isSaving.set(false);
        this.successMessage.set(successMessage);
        onSuccess?.();
        this.loadUsers();
      },
      error: (error: unknown) => {
        this.isSaving.set(false);
        if (onError) {
          onError(error);
        } else {
          this.errorDetails.set(getHttpErrorDetails(error, 'User action failed.'));
        }
      },
    });
  }
}
