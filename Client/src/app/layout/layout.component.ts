import {Component, HostListener, signal} from '@angular/core';
import {NgIf, CommonModule} from '@angular/common';
import {AuthManager} from '@core/auth/auth.manager';
import {Router, RouterOutlet, RouterModule} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';
import {MatIcon} from '@angular/material/icon';
@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgIf, RouterOutlet, RouterModule, CommonModule, MatIcon],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
})
/**
 * Layout component that manages the overall application layout,
 * including the user menu, language switching, and logout functionality.
 */
export class LayoutComponent {

  // Flag to show or hide the user menu
  showMenu = false;
  // NOVÁ VLASTNOST: pro zobrazení dropdownu jazyka
  showLangDropdown = false;
  // Current active language (mock signal for UI update)
  activeLanguage = signal<'en' | 'cs'>('en');

  // Timeout reference for closing the menu
  closeTimeout: any;

  // Current authenticated user (read-only signal from AuthManager)
  user;


  /**
   * Constructor to inject dependencies.
   */
  constructor(
    private authService: AuthManager,
    private router: Router,
    protected toastr: ToastrService) {

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

  toggleLangDropdown() {
    this.showLangDropdown = !this.showLangDropdown;
  }
  openLangDropdown() {
    if (this.closeTimeout) clearTimeout(this.closeTimeout);
    this.showLangDropdown = true;
  }
  closeLangDropdown() {
    this.closeTimeout = setTimeout(() => (this.showLangDropdown = false), 250);
  }

  /**
   * MOCK: Switches the application language (English/Czech).
   * @param lang The language code ('en' or 'cs').
   */
  switchLanguage(lang: 'en' | 'cs') {
    this.activeLanguage.set(lang);
    this.showLangDropdown = false; // Zavřít po výběru
    this.toastr.info(`Language switched to ${lang.toUpperCase()}. (MOCK)`);
  }

  /**
   * Detect clicks outside the user menu to close it.
   * @param event Click event
   */
  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;
    // Kontrola kliknutí mimo OBA DROPDOWNY
    if (!target.closest('.user-menu-wrapper')) {
      this.showMenu = false;
    }
    if (!target.closest('.language-dropdown-wrapper')) {
      this.showLangDropdown = false;
    }
  }

  /**
   * Logout the user and navigate to the authentication page.
   */
  logout() {
    this.authService.logout().then(() => {
        AuthRoutingHelper.navigateToAuth(this.router).then(
          () => this.toastr.success('Logged out successfully.')
        )
      }
    );
  }
}
