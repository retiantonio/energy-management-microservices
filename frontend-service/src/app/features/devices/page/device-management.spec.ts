import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/auth.models';
import { DeviceManagementPage } from './device-management';

describe('DeviceManagementPage', () => {
  let authService: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceManagementPage],
      providers: [provideHttpClient(), provideHttpClientTesting()],
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

  it('should show owned device cards and no admin actions to a client', () => {
    setSession('CLIENT');
    const fixture = TestBed.createComponent(DeviceManagementPage);
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
    expect(page.querySelector('h1')?.textContent).toContain('My devices');
    expect(page.querySelector('.device-card h2')?.textContent).toContain('Desk lamp');
    expect(page.querySelector('.card-actions')).toBeNull();
    expect(page.querySelector('.toolbar > button')).toBeNull();
  });

  it('should load users and expose device actions to an admin', () => {
    setSession('ADMIN');
    const fixture = TestBed.createComponent(DeviceManagementPage);
    fixture.detectChanges();

    httpTesting.expectOne('/api/devices').flush([]);
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
    expect(page.querySelector('h1')?.textContent).toContain('Device management');

    const createButton = page.querySelector('.toolbar > button') as HTMLButtonElement;
    expect(createButton).not.toBeNull();
    createButton.click();
    fixture.detectChanges();
    expect(page.querySelector('select[name="createOwner"]')).not.toBeNull();
  });
});
