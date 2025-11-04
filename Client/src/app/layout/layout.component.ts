import {Component, HostListener} from '@angular/core';
import {NgIf} from '@angular/common';
import {AuthService} from '@core/auth/auth.service';
import {Router, RouterOutlet} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

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

  constructor(public authService: AuthService, private router: Router, private toastr: ToastrService) {
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

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu')) {
      this.showMenu = false;
    }
  }

  logout() {
    this.authService.logout().then(r => {
        this.router.navigate(['/']).then(() => {
          this.toastr.success('Logged out successfully!');
        });
      }
    );

  }
}
