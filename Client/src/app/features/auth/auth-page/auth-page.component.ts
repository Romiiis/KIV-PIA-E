import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '@core/auth/auth.service';
import {NotificationService} from '@core/services/notification.service';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {UserRoleDomain} from '@core/models/userRole.model';

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
      this.auth.login(email, password)
        .then(user => {
          this.notify.success('Login successful!');
          // Redirect based on role
          switch (user?.role) {
            case UserRoleDomain.CUSTOMER:
              this.router.navigate(['/customer']);
              break;
            case UserRoleDomain.TRANSLATOR:
              this.router.navigate(['/translator']);
              break;
            case UserRoleDomain.ADMINISTRATOR:
              this.router.navigate(['/admin']);
              break;
            default:
              this.router.navigate(['/']);
          }
        })
        .catch(err => {
          console.error('Login error:', err);
          this.notify.error('Login failed. Please check your credentials.');
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
