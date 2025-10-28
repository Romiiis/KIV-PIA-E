import { Component, HostListener, signal } from '@angular/core';
import { NgIf } from '@angular/common';
import { AuthService } from '@core/auth/auth.service';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgIf, RouterOutlet],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
})
export class LayoutComponent {
  showMenu = false;
  closeTimeout: any;

  constructor(public authService: AuthService) {}

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
    this.closeTimeout = setTimeout(() => (this.showMenu = false), 250);
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu')) {
      this.showMenu = false;
    }
  }

  logout() {
    this.authService.logout();
  }
}
