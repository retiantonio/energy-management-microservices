import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../authentication/pages/login/services/auth.service';
import { DeverBrand } from '../../../../shared/components/dever-brand/dever-brand';

@Component({
  imports: [DeverBrand, RouterLink, RouterLinkActive],
  selector: 'app-dashboard-sidebar',
  styleUrl: './dashboard-sidebar.scss',
  templateUrl: './dashboard-sidebar.html',
})
export class DashboardSidebar {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly session = this.authService.authResponse;
  protected readonly isAdmin = computed(() => this.session()?.role === 'ADMIN');

  protected logout(): void {
    this.authService.logout();
    void this.router.navigateByUrl('/login');
  }
}
