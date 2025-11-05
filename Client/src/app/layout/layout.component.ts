import {Component, HostListener} from '@angular/core';
import {NgIf} from '@angular/common';
import {AuthManager} from '@core/auth/auth.manager';
import {Router, RouterOutlet} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgIf, RouterOutlet],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
})
/**
 * Layout component that manages the overall application layout,
 * including the user menu and logout functionality.
 * Handles menu toggling and outside click detection.
 */
export class LayoutComponent {

  // Flag to show or hide the user menu
  showMenu = false;

  // Timeout reference for closing the menu
  closeTimeout: any;

  // Current authenticated user
  user;


  /**
   * Constructor to inject dependencies.
   * @param authService AuthManager for authentication operations.
   * @param router Router for navigation.
   * @param toastr ToastrService for displaying notifications.
   */
  constructor(
    private authService: AuthManager,
    private router: Router,
    private toastr: ToastrService) {

    this.user = this.authService.user();
  }

  /**
   * Toggle the visibility of the user menu.
   */
  toggleMenu() {
    this.showMenu = !this.showMenu;
  }

  /**
   * Open the user menu and clear any pending close timeout.
   */
  openMenu() {
    if (this.closeTimeout) {
      clearTimeout(this.closeTimeout);
    }
    this.showMenu = true;
  }

  /**
   * Close the user menu after a short delay.
   */
  closeMenu() {
    this.closeTimeout = setTimeout(() => (this.showMenu = false), 250);
  }

  /**
   * Detect clicks outside the user menu to close it.
   * @param event Click event
   */
  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu')) {
      this.showMenu = false;
    }
  }

  /**
   * Logout the user and navigate to the authentication page.
   */
  logout() {
    this.authService.logout().then(r => {
        AuthRoutingHelper.navigateToAuth(this.router).then(
          () => this.toastr.success('Logged out successfully.')
        )
      }
    );

  }
}
