import {Injectable} from '@angular/core';
import {ApiResult, BaseApiService} from './base-api.service';
import {loginUser, logoutUser, refreshToken, registerUser,} from '@generated/auth/auth';
import {getCurrentUser} from '@generated/me/me';
import type {AuthJWTResponse, LoginUserRequest, RegisterUserRequest,} from '@generated/model';
import {UserMapper} from '@api/mappers/user.mapper';
import {UserDomain} from '@core/models/user.model';

@Injectable({providedIn: 'root'})
export class AuthApiService extends BaseApiService {
  async login(payload: LoginUserRequest): Promise<ApiResult<AuthJWTResponse>> {
    try {
      const res = await loginUser(payload, this.defaultOptions);
      return this.handleResponse<AuthJWTResponse>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message};
    }
  }

  async register(
    payload: RegisterUserRequest
  ): Promise<ApiResult<AuthJWTResponse>> {
    try {
      const res = await registerUser(payload, this.defaultOptions);
      return this.handleResponse<AuthJWTResponse>(res, [201]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message};
    }
  }

  async logout(): Promise<ApiResult<void>> {
    try {
      const res = await logoutUser(this.defaultOptions);
      return this.handleResponse<void>(res, [204]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message};
    }
  }

  async refresh(): Promise<ApiResult<AuthJWTResponse>> {
    try {
      const res = await refreshToken(this.defaultOptions);
      return this.handleResponse<AuthJWTResponse>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message};
    }
  }

  /**
   * Fetch current authenticated user
   * Maps API user → domain user
   */
  async me(): Promise<ApiResult<UserDomain>> {
    try {
      const res = await getCurrentUser(this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = UserMapper.mapApiUserToUser(res.data);
        return this.handleResponse<UserDomain>({...res, data: mapped}, [200]);
      }

      return this.handleResponse<UserDomain>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message};
    }
  }
}
