import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../../../../core/services/auth.service';
import { AuthResponse } from '../../../../../core/models/auth.models';
import { DashboardOverview } from './overview';

describe('DashboardOverview', () => {
  let authService: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardOverview],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    authService = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
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

  it('should load and show a client their devices without loading users', () => {
    setSession('CLIENT');
    const fixture = TestBed.createComponent(DashboardOverview);
    fixture.detectChanges();

    httpTesting.expectNone('/api/users');
    httpTesting.expectOne('/api/devices').flush([
      {
        id: 'device-1',
        name: 'Desk lamp',
        description: 'Office light',
        maxEnergyConsumption: 4.5,
        ownerUserId: '00000000-0000-0000-0000-000000000001',
      },
    ]);
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('h1')?.textContent).toContain('Welcome, johndoe');
    expect(page.querySelector('#devices-heading')?.textContent).toContain('My devices');
    expect(page.querySelector('#users-heading')).toBeNull();
    expect(page.querySelector('.device-card h3')?.textContent).toContain('Desk lamp');
  });

  it('should load device and user cards for an admin', () => {
    setSession('ADMIN');
    const fixture = TestBed.createComponent(DashboardOverview);
    fixture.detectChanges();

    httpTesting.expectOne('/api/devices').flush([
      {
        id: 'device-1',
        name: 'Desk lamp',
        description: 'Office light',
        maxEnergyConsumption: 4.5,
        ownerUserId: 'user-1',
      },
    ]);
    httpTesting.expectOne('/api/users').flush([
      {
        uuid: 'user-1',
        name: 'Jane Doe',
        email: 'jane@example.com',
        address: '1 Main Street',
        role: 'CLIENT',
      },
    ]);
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('#devices-heading')?.textContent).toContain('Devices');
    expect(page.querySelector('#users-heading')).not.toBeNull();
    expect(page.querySelector('.device-card h3')?.textContent).toContain('Desk lamp');
    expect(page.querySelector('.user-card h3')?.textContent).toContain('Jane Doe');
    expect(page.querySelector('.device-card dl div:nth-child(2) dd')?.textContent).toContain(
      'Jane Doe',
    );
  });

  it('should filter client devices locally across their details', async () => {
    setSession('CLIENT');
    const fixture = TestBed.createComponent(DashboardOverview);
    fixture.detectChanges();
    httpTesting.expectOne('/api/devices').flush([
      {
        id: 'device-1',
        name: 'Desk lamp',
        description: 'Office light',
        maxEnergyConsumption: 4.5,
        ownerUserId: '00000000-0000-0000-0000-000000000001',
      },
      {
        id: 'device-2',
        name: 'Ceiling fixture',
        description: 'Main room',
        maxEnergyConsumption: 8,
        ownerUserId: '00000000-0000-0000-0000-000000000001',
      },
    ]);
    fixture.detectChanges();
    await fixture.whenStable();

    const page = fixture.nativeElement as HTMLElement;
    const searchInput = page.querySelector(
      'input[name="overviewDeviceSearchTerm"]',
    ) as HTMLInputElement;
    searchInput.value = 'office';
    searchInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();

    expect(page.querySelectorAll('app-device-overview-card').length).toBe(1);
    expect(page.querySelector('app-device-overview-card h3')?.textContent).toContain('Desk lamp');
    httpTesting.expectNone('/api/devices?name=office');
  });
});
