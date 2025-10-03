import {Component, HostListener, Input} from '@angular/core';
import {NgIf} from '@angular/common';
import {User} from '@core/models/user.model';
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
  @Input() loggedUser!: User
  showMenu = false;
  closeTimeout: any;

  constructor(
     public authService: AuthService) {

    this.loggedUser = {
      name: "Jon Doe",
      emailAddress: "JD@gmail.com",
      role: "customer",
      id: "jd"
    }
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
