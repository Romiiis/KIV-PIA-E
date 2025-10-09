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

  loggedUser: User | undefined

  constructor(private router: Router) {
  }

  login(email: string, password: string): Observable<boolean> {

    const found = MOCKS.find(
      m => m.username == email && m.password == password
    );

    console.log('Login attempt:', {email, password, found});

    if (found) {
      this.loggedUser = found.user as User;
      return of(true);
    }

    this.loggedUser = undefined;
    return of(false);

  }

  logout() {
    this.router.navigate(['/auth']);
  }


  getRole(): string | null {
    return this.loggedUser?.role || null;
  }

  isLoggedIn(): boolean {
    return !!this.loggedUser;
  }


  getUser() {
    return this.loggedUser;
  }
}
