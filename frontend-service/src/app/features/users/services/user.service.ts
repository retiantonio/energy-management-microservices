import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user.models';
import { ApiEndpoints } from '../../../core/constants/api-endpoints';

export interface CreateUserRequest {
  role: User['role'];
  username: string;
  password: string;
  name: string;
  email: string;
  address: string;
}

export interface UpdateUserRequest {
  username?: string;
  password?: string;
  name?: string;
  email?: string;
  address?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = ApiEndpoints.users;
  private readonly http = inject(HttpClient);

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  search(name?: string, email?: string): Observable<User[]> {
    let params = new HttpParams();
    const normalizedName = name?.trim();
    const normalizedEmail = email?.trim();
    if (normalizedName) {
      params = params.set('name', normalizedName);
    }
    if (normalizedEmail) {
      params = params.set('email', normalizedEmail);
    }

    return this.http.get<User[]>(this.apiUrl, { params });
  }

  create(request: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, request);
  }

  update(uuid: string, request: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${encodeURIComponent(uuid)}`, request);
  }

  delete(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${encodeURIComponent(uuid)}`);
  }
}
