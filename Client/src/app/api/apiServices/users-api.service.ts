import { Injectable } from '@angular/core';
import { BaseApiService, ApiResult } from './base-api.service';
import {
  listAllUsers,
  getUserDetails,
  changeUserRole,
} from '@generated/users/users';
import type {
  ListAllUsersParams,
  ListUsersResponse,
  User,
  InitializeUserRequest,
} from '@generated/model';
import { UserMapper } from '@api/mappers/user.mapper';
import { UserDomain } from '@core/models/user.model';

@Injectable({ providedIn: 'root' })
export class UsersApiService extends BaseApiService {
  /**
   * Get all users (ADMIN only)
   */
  async getAll(
    params?: ListAllUsersParams
  ): Promise<ApiResult<UserDomain[]>> {
    try {
      const res = await listAllUsers(params, this.defaultOptions);

      if (res.status === 200 && Array.isArray(res.data)) {
        const mapped = res.data.map(UserMapper.mapApiUserToUser);
        return this.handleResponse<UserDomain[]>({ ...res, data: mapped }, [200]);
      }

      // pokud API vrátí něco nečekaného, vrať prázdné pole
      return this.handleResponse<UserDomain[]>({ ...res, data: [] }, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Get user by ID (any role can view their own)
   */
  async getById(id: string): Promise<ApiResult<UserDomain>> {
    try {
      const res = await getUserDetails(id, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = UserMapper.mapApiUserToUser(res.data);
        return this.handleResponse<UserDomain>({ ...res, data: mapped }, [200]);
      }

      return this.handleResponse<UserDomain>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Change role for a specific user (only if current role is null)
   */
  async changeRole(
    id: string,
    payload: InitializeUserRequest
  ): Promise<ApiResult<void>> {
    try {
      const res = await changeUserRole(id, payload, this.defaultOptions);
      return this.handleResponse<void>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }
}
