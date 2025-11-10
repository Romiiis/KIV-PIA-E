import {Component, HostListener, signal} from '@angular/core';
import {NgIf, CommonModule} from '@angular/common';
import {AuthManager} from '@core/auth/auth.manager';
import {Router, RouterOutlet, RouterModule} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';
import {MatIcon} from '@angular/material/icon';
import {
  TranslatorSettingsModalComponent
} from '@features/translator/translator-settings.component/translator-settings-modal.component';
import {MatDialog} from '@angular/material/dialog';
import {LanguageService, SupportedLanguage} from '@core/services/language.service';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgIf, RouterOutlet, RouterModule, CommonModule, MatIcon, TranslatePipe],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
})

export class LayoutComponent {

  showMenu = false;
  showLangDropdown = false;
  closeTimeout: any;
  user;

  protected readonly SupportedLanguage = SupportedLanguage;

  constructor(
    private authService: AuthManager,
    private router: Router,
    protected toastr: ToastrService,
    public dialog: MatDialog,
    public languageService: LanguageService,
    private translate: TranslateService
  ){

    this.user = this.authService.user();
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



  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;

    if (!target.closest('.user-menu-wrapper')) {
      this.showMenu = false;
    }
    if (!target.closest('.language-dropdown-wrapper')) {
      this.showLangDropdown = false;
    }
  }

  openSettingsModal(): void {
    this.dialog.open(TranslatorSettingsModalComponent, {
      width: '600px',
      maxWidth: '95vw',
      panelClass: 'clean-dialog-panel',
      disableClose: true
    });
  }


  logout() {
    this.authService.logout().then(() => {
        AuthRoutingHelper.navigateToAuth(this.router).then(
          () => {
            let logoutSuccessText = this.translate.instant('layout.notifications.logoutSuccess_text');
            this.toastr.success(logoutSuccessText);
          }
        )
      }
    );
  }
}
