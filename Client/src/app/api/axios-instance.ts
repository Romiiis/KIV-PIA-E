import axios, {AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse} from 'axios';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';

/**
 * Axios instance with interceptor to handle 401 responses by attempting token refresh.
 * If refresh fails, redirects to /auth.
 */
const instance: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

// Flag to indicate if a token refresh is in progress
let isRefreshing = false;

// Promise that resolves when the token refresh is complete
let refreshPromise: Promise<boolean> | null = null;

// Handler function to refresh tokens
let refreshHandler: (() => Promise<boolean>) | null = null;

/**
 * Registers a handler function to refresh authentication tokens.
 * @param handler Function that returns a promise resolving to true if refresh succeeded, false otherwise.
 */
export function setRefreshHandler(handler: () => Promise<boolean>) {
  refreshHandler = handler;
}

/**
 * Response interceptor to handle 401 Unauthorized errors by attempting to refresh tokens.
 * If the refresh is successful, retries the original request.
 * If the refresh fails, redirects the user to the /auth page.
 */
instance.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const original = error.config as AxiosRequestConfig & { _retry?: boolean };
    const status = error.response?.status;

    // If not a 401 error or already retried, reject the promise
    if (status !== 401 || original._retry) {
      return Promise.reject(error);
    }

    // Mark the request as retried
    original._retry = true;

    // If no refresh handler is registered, log an error and reject
    if (!refreshHandler) {
      console.error('[No refresh handler registered!');
      return Promise.reject(error);
    }

    // If a refresh is not already in progress, start one
    if (!isRefreshing) {
      // Set the refreshing flag and create the refresh promise
      isRefreshing = true;

      // Call the refresh handler
      refreshPromise = refreshHandler()
        .then(() => true)
        .catch((err) => {
          console.warn('Refresh failed:', err);
          return false;
        })
        .finally(() => {
          isRefreshing = false;
        });
    }

    // Wait for the refresh to complete
    const ok = await refreshPromise;

    // If refresh succeeded, retry the original request
    if (ok) {
      console.log('Token refreshed successfully');
      return instance(original);
    } else {
      // If refresh failed, log a warning and redirect to /auth
      console.warn('Refresh failed – redirecting to /auth');

      // Redirect to /auth page
      window.location.href = AuthRoutingHelper.AUTH_PATH;
      return Promise.reject(error);
    }
  }
);

/**
 * Generic function to make API requests using the Axios instance.
 * @param config Axios request configuration.
 */
export const axiosInstance = async <T = unknown>(
  config: AxiosRequestConfig
): Promise<AxiosResponse<T>> => instance.request<T>(config);

export default instance;
