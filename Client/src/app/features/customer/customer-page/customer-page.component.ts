import { Component, HostListener } from '@angular/core';
import {NgIf} from '@angular/common';
import {NewProjectComponent} from '@features/customer/new-project/new-project.component';

@Component({
  selector: 'app-customer-page',
  templateUrl: './customer-page.component.html',
  imports: [
    NgIf,
    NewProjectComponent
  ],
  styleUrls: ['./customer-page.component.css']
})
export class CustomerPageComponent {
  mode: 'new' | 'list' = 'list';
  fullName = 'John Doe';
  role = 'Customer';
  email = 'rpejs@students.zcu.cz';
  showMenu = false;
  closeTimeout: any;

  switchMode(mode: 'new' | 'list') {
    this.mode = mode;
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
  }

  addProject($event: any) {
    
  }
}
