import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'auth_token';
  private roleKey = 'auth_role';

  constructor(private router: Router) {}

  login(username: string, password: string): Observable<boolean> {
    // MOCK – nahradíš voláním API
    if (username === 'admin' && password === 'admin') {
      localStorage.setItem(this.tokenKey, 'fake-jwt-token');
      localStorage.setItem(this.roleKey, 'admin');
      return of(true);
    } else {
      localStorage.setItem(this.tokenKey, 'fake-jwt-token');
      localStorage.setItem(this.roleKey, 'customer');
      return of(true);
    }
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRole(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
