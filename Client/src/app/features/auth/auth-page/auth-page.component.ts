import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators, FormGroup, ReactiveFormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from '@core/auth/auth.service';
import {NotificationService} from '@core/services/notification.service';
import {NgIf, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html',
  imports: [
    ReactiveFormsModule,
    NgIf,
    NgOptimizedImage
  ],
  styleUrls: ['./auth-page.component.css']
})
export class AuthPageComponent implements OnInit {
  mode: 'login' | 'register' = 'login';
  loginForm!: FormGroup;
  registerForm!: FormGroup;



  ngOnInit(): void {
    // login form
    this.loginForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });

    // register form
    this.registerForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: ['customer', Validators.required]
    });

  }

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private notify: NotificationService,
    private router: Router
  ) {}

  switchMode(newMode: 'login' | 'register') {
    this.mode = newMode;
  }

  submitLogin() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.auth.login(email!, password!).subscribe(() => {
        const role = this.auth.getRole();
        this.router.navigate([`/${role}`]);
      });
    }
  }

  submitRegister() {
    if (this.registerForm.valid) {
      console.log('New user:', this.registerForm.value);
      this.notify.success('Registration successful! You can now log in.');
      this.switchMode('login');
    }
  }
}
