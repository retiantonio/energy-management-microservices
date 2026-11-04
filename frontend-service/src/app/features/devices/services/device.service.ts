import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateDeviceRequest, Device, UpdateDeviceRequest } from '../models/device.models';
import { ApiEndpoints } from '../../../core/constants/api-endpoints';

@Injectable({ providedIn: 'root' })
export class DeviceService {
  private readonly apiUrl = ApiEndpoints.devices;
  private readonly http = inject(HttpClient);

  list(): Observable<Device[]> {
    return this.http.get<Device[]>(this.apiUrl);
  }

  search(name?: string): Observable<Device[]> {
    let params = new HttpParams();
    const normalizedName = name?.trim();
    if (normalizedName) {
      params = params.set('name', normalizedName);
    }

    return this.http.get<Device[]>(this.apiUrl, { params });
  }

  create(request: CreateDeviceRequest): Observable<Device> {
    return this.http.post<Device>(this.apiUrl, request);
  }

  update(id: string, request: UpdateDeviceRequest): Observable<Device> {
    return this.http.put<Device>(`${this.apiUrl}/${encodeURIComponent(id)}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${encodeURIComponent(id)}`);
  }

  assign(id: string, userId: string): Observable<Device> {
    return this.http.put<Device>(`${this.apiUrl}/${encodeURIComponent(id)}/assignment`, { userId });
  }

  unassign(id: string): Observable<Device> {
    return this.http.delete<Device>(`${this.apiUrl}/${encodeURIComponent(id)}/assignment`);
  }
}
