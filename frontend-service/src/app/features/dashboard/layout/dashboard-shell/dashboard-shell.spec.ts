import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { AuthResponse } from '../../../../core/models/auth.models';
import { DashboardShell } from './dashboard-shell';

describe('DashboardShell', () => {
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardShell],
      providers: [provideHttpClient(), provideRouter([])],
    }).compileComponents();
    authService = TestBed.inject(AuthService);
  });

  function setSession(role: AuthResponse['role']): void {
    authService.authResponse.set({
      token: 'test-token',
      username: 'johndoe',
      userId: '00000000-0000-0000-0000-000000000001',
      role,
      expiresInSeconds: 3600,
    });
  }

  it('should show Devices and hide Users for a client', () => {
    setSession('CLIENT');
    const fixture = TestBed.createComponent(DashboardShell);
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('a[href="/dashboard/devices"]')).not.toBeNull();
    expect(page.querySelector('a[href="/dashboard/users"]')).toBeNull();
  });

  it('should show Users for an admin', () => {
    setSession('ADMIN');
    const fixture = TestBed.createComponent(DashboardShell);
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('a[href="/dashboard/users"]')).not.toBeNull();
  });
});
