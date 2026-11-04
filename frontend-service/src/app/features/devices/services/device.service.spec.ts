import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CreateDeviceRequest, Device } from '../models/device.models';
import { DeviceService } from './device.service';

describe('DeviceService', () => {
  let deviceService: DeviceService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DeviceService, provideHttpClient(), provideHttpClientTesting()],
    });

    deviceService = TestBed.inject(DeviceService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should list all devices without a server-side filter', () => {
    deviceService.list().subscribe();

    const request = httpTesting.expectOne('/api/devices');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.keys()).toEqual([]);
    request.flush([]);
  });

  it('should send a device name filter', () => {
    deviceService.search('lamp').subscribe();

    const request = httpTesting.expectOne('/api/devices?name=lamp');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('name')).toBe('lamp');
    request.flush([]);
  });

  it('should send create, update, and delete requests', () => {
    const device: Device = {
      id: 'device-1',
      name: 'Desk lamp',
      description: 'Office light',
      maxEnergyConsumption: 4.5,
      ownerUserId: null,
    };
    const createRequest: CreateDeviceRequest = {
      name: device.name,
      description: device.description,
      maxEnergyConsumption: device.maxEnergyConsumption,
      ownerUserId: device.ownerUserId,
    };

    deviceService.create(createRequest).subscribe();
    const create = httpTesting.expectOne('/api/devices');
    expect(create.request.method).toBe('POST');
    expect(create.request.body).toEqual(createRequest);
    create.flush(device);

    deviceService
      .update(device.id, {
        name: 'Desk lamp 2',
        maxEnergyConsumption: 5,
      })
      .subscribe();
    const update = httpTesting.expectOne('/api/devices/device-1');
    expect(update.request.method).toBe('PUT');
    update.flush({ ...device, name: 'Desk lamp 2' });

    deviceService.delete(device.id).subscribe();
    const remove = httpTesting.expectOne('/api/devices/device-1');
    expect(remove.request.method).toBe('DELETE');
    remove.flush(null);
  });

  it('should send assignment and unassignment requests', () => {
    const device: Device = {
      id: 'device-1',
      name: 'Desk lamp',
      description: null,
      maxEnergyConsumption: 4.5,
      ownerUserId: 'user-1',
    };

    deviceService.assign(device.id, 'user-1').subscribe();
    const assign = httpTesting.expectOne('/api/devices/device-1/assignment');
    expect(assign.request.method).toBe('PUT');
    expect(assign.request.body).toEqual({ userId: 'user-1' });
    assign.flush(device);

    deviceService.unassign(device.id).subscribe();
    const unassign = httpTesting.expectOne('/api/devices/device-1/assignment');
    expect(unassign.request.method).toBe('DELETE');
    unassign.flush({ ...device, ownerUserId: null });
  });
});
