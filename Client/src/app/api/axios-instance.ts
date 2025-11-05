import axios, {AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse,} from 'axios';


const instance: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, // 👉 nutné pro cookie autentizaci
});

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

instance.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
    const status = error.response?.status;

    // pokud to není 401 nebo už retry proběhl → předej dál
    if (status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = axios
        .post('http://localhost:8080/auth/refresh', {}, {withCredentials: true})
        .then(() => true)
        .catch(() => false)
        .finally(() => {
          isRefreshing = false;
        });
    }

    const ok = await refreshPromise;

    if (ok) {
      console.log('✅ Token refreshed successfully');
      // ✅ nový access token je v cookies, retry request
      return instance(originalRequest);
    } else {
      // ❌ refresh selhal → redirect na login
      window.location.href = '/auth';
      return Promise.reject(error);
    }
  }
);

// ✅ univerzální wrapper
export const axiosInstance = async <T = unknown>(
  config: AxiosRequestConfig
): Promise<AxiosResponse<T>> => {
  return instance.request<T>(config);
};

export default instance;
