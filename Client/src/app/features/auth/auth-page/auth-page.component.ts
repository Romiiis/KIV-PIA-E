import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthManager} from '@core/auth/auth.manager';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';

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

/**
 * Authentication page component for user login and registration.
 * Handles form submission and navigation based on authentication status.
 */
export class AuthPageComponent implements OnInit {

  // Current mode: 'login' or 'register'.
  mode: 'login' | 'register' = 'login';

  // Login form group.
  loginForm!: FormGroup;

  // Registration form group.
  registerForm!: FormGroup;


  /**
   * Initialize component and forms.
   * Sets up reactive forms for login and registration with validation.
   */
  ngOnInit(): void {
    this.loginForm = this.fb.group({

      // Login form with validation rules.
      email: ['', Validators.required],
      password: ['', Validators.required]
    });

    // Registration form with validation rules.
    this.registerForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    });

  }

  /**
   * Constructor for AuthPageComponent.
   * @param fb FormBuilder for creating reactive forms.
   * @param auth AuthManager for handling authentication logic.
   * @param router Router for navigation.
   * @param toastr ToastrService for displaying notifications.
   */
  constructor(
    private fb: FormBuilder,
    private auth: AuthManager,
    private router: Router,
    private toastr: ToastrService
  ) {
  }

  /**
   * Switch between login and registration modes.
   * @param newMode The mode to switch to ('login' or 'register').
   */
  switchMode(newMode: 'login' | 'register') {
    this.mode = newMode;
  }

  /**
   * Submit the login form.
   * Validates the form and calls the AuthManager to log in the user.
   * On success, navigates to the appropriate path based on user role.
   * Displays success or error notifications.
   */
  submitLogin() {
    // Check if the login form is valid.
    if (this.loginForm.valid) {

      // Extract email and password from the form.
      const {email, password} = this.loginForm.value;

      // Call the AuthManager to log in.
      this.auth.login(email, password)

        // Handle successful login.
        .then(() => {

          // Display success notification.
          this.toastr.success('Login successful!');

          // Get the current user.
          let currentUser = this.auth.user()

          // Check if user information is available.
          if (!currentUser) {
            this.toastr.error('Unable to retrieve user information.');
          }

          // Redirect based on user role.
          AuthRoutingHelper.navigateToRole(this.router, currentUser!.role).then();
        })

        // Handle login failure.
        .catch(() => {
          this.toastr.error('Login failed. Please check your credentials.');
        });

    }
  }


  /**
   * * Submit the registration form.
   * Validates the form and calls the AuthManager to register the user.
   * On success, navigates to the appropriate path based on user role.
   * Displays success or error notifications.
   */
  submitRegister() {

    // Check if the registration form is valid.
    if (this.registerForm.valid) {
      // Call the AuthManager to register.
      this.auth.register(
        this.registerForm.value.email,
        this.registerForm.value.password,
        this.registerForm.value.fullname
      )
        // Handle successful registration.
        .then(() => {
          // Display success notification.
          this.toastr.success('Registration successful!');

          // Get the current user.
          let currentUser = this.auth.user()

          // Redirect based on user role.
          if (!currentUser) {
            this.toastr.error('Unable to retrieve user information.');
          }
          // Redirect based on user role.
          AuthRoutingHelper.navigateToRole(this.router, currentUser!.role).then();
        });

    }
  }


}
