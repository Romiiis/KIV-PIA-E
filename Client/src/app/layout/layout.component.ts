import {Component, HostListener, Input} from '@angular/core';
import {NgIf} from '@angular/common';
import {UserDomain} from '@core/models/user.model';
import {RouterOutlet} from '@angular/router';
import {AuthService} from '@core/auth/auth.service';

@Component({
  selector: 'app-layout',
  imports: [
    NgIf,
    RouterOutlet
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent {
  loggedUser!: UserDomain
  showMenu = false;
  closeTimeout: any;

  constructor(
    public authService: AuthService) {
    this.loggedUser = this.authService.getUser()!;
  }


  toggleMenu() {
    this.showMenu = !this.showMenu;
  }

  openMenu() {
    if (this.closeTimeout) {
      clearTimeout(this.closeTimeout);
    }
    this.showMenu = true;
  }


  closeMenu() {
    this.closeTimeout = setTimeout(() => {
      this.showMenu = false;
    }, 250); // 250 ms delay
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu')) {
      this.showMenu = false;
    }
  }

  logout() {
    console.log('Logging out...');
    this.authService.logout()
  }


}
