import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {UserDomain} from '@core/models/user.model';
import {UserRoleDomain} from '@core/models/userRole.model';


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

  loggedUser: UserDomain | undefined

  constructor(private router: Router) {
  }

  login(email: string, password: string): Observable<boolean> {

    //TODO Replace with real API call

    this.loggedUser = undefined;
    return of(false);

  }

  logout() {
    this.router.navigate(['/auth']);
  }


  getRole(): UserRoleDomain | null {
    return this.loggedUser?.role || null;
  }

  isLoggedIn(): boolean {
    return !!this.loggedUser;
  }


  getUser() {
    return this.loggedUser;
  }
}
