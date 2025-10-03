import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {User} from '@core/models/user.model';


const MOCKS = [
  {
    username: "admin",
    password: "admin",
    user: {
      name: "Admin admin",
      role: "admin"
    }
  },
  {
    username: "customer",
    password: "customer",
    user: {
      name: "John Customer",
      role: "customer"
    }
  },
  {
    username: "translator",
    password: "translator",
    user: {
      name: "Jane Translator",
      role: "translator"
    }
  }
];


@Injectable({providedIn: 'root'})
export class AuthService {
  private tokenKey = 'auth_token';
  private roleKey = 'auth_role';


  loggedUser: User | undefined

  constructor(private router: Router) {
  }

  login(email: string, password: string): Observable<boolean> {
    console.log(email, password)

    const found = MOCKS.find(
      m => m.username == email && m.password == password
    );

    console.log(found)

    if (found) {
      this.loggedUser = found.user as User;

      localStorage.setItem(this.tokenKey, 'fake-jwt-token');
      localStorage.setItem(this.roleKey, this.loggedUser.role);
      return of(true);
    }

    this.loggedUser = undefined;
    return of(false);

  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    this.router.navigate(['/auth']);
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
