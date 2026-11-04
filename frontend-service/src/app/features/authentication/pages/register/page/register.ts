import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../login/services/auth.service';
import { getAuthErrorDetails } from '../../../shared/auth-error-message';
import { DeverBrand } from '../../../../../shared/components/dever-brand/dever-brand';
import { HttpErrorDetails } from '../../../../../core/utils/http-error-message';
import { HttpErrorNotice } from '../../../../../core/components/http-error-notice/http-error-notice';

@Component({
  imports: [DeverBrand, FormsModule, HttpErrorNotice, RouterLink],
  selector: 'app-register-page',
  styleUrl: '../../../shared/auth-page.scss',
  templateUrl: './register.html',
})
export class RegisterPage {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  protected readonly authResponse = this.authService.authResponse;
  protected readonly isSubmitting = signal(false);
  protected readonly isRegistered = signal(false);
  protected readonly errorDetails = signal<HttpErrorDetails | null>(null);
  protected readonly missingFieldsError = signal('');
  protected readonly showPassword = signal(false);
  
  protected username = '';
  protected password = '';
  protected name = '';
  protected email = '';
  protected address = '';

  protected register(): void {
    const username = this.username.trim();
    const name = this.name.trim();
    const email = this.email.trim();
    const address = this.address.trim();

    if (this.isSubmitting()) {
      return;
    }

    if (!username || !this.password || !name || !email || !address) {
      this.errorDetails.set(null);
      this.missingFieldsError.set('Please fill in all required fields.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorDetails.set(null);
    this.missingFieldsError.set('');

    this.authService
      .register({
        username,
        password: this.password,
        name,
        email,
        address,
      })
      .subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isRegistered.set(true);
          void this.router.navigateByUrl('/dashboard');
        },
        error: (error: unknown) => {
          this.isSubmitting.set(false);
          this.errorDetails.set(getAuthErrorDetails(error));
        },
      });
  }
}
