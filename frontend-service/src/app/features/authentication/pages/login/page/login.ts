import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { getAuthErrorDetails } from '../../../shared/auth-error-message';
import { HttpErrorDetails } from '../../../../../core/utils/http-error-message';
import { HttpErrorNotice } from '../../../../../core/components/http-error-notice/http-error-notice';
import { DeverBrand } from '../../../../../shared/components/dever-brand/dever-brand';

@Component({
  imports: [DeverBrand, FormsModule, HttpErrorNotice, RouterLink],
  selector: 'app-login-page',
  styleUrl: '../../../shared/auth-page.scss',
  templateUrl: './login.html',
})
export class LoginPage {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  protected readonly isSubmitting = signal(false);
  protected readonly errorDetails = signal<HttpErrorDetails | null>(null);
  protected readonly missingFieldsError = signal('');

  protected showPassword = signal(false);
  protected username = '';
  protected password = '';

  protected login(): void {
    const username = this.username.trim();
    if (this.isSubmitting()) {
      return;
    }

    if (!username || !this.password) {
      this.errorDetails.set(null);
      this.missingFieldsError.set('Please fill in both your username and password.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorDetails.set(null);
    this.missingFieldsError.set('');

    this.authService.login({ username, password: this.password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        void this.router.navigateByUrl('/dashboard');
      },
      error: (error: unknown) => {
        this.isSubmitting.set(false);
        this.errorDetails.set(getAuthErrorDetails(error));
      },
    });
  }
}
