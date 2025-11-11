import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthManager} from '@core/auth/auth.manager';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';
import {MatIcon} from '@angular/material/icon';
import {LanguageService, SupportedLanguage} from '@core/services/language.service';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html',
  imports: [
    ReactiveFormsModule,
    NgIf,
    NgOptimizedImage,
    MatIcon,
    TranslatePipe
  ],
  styleUrls: ['./auth-page.component.css']
})

/**
 * Authentication page component for user login and registration.
 * Handles form submission and navigation based on authentication status.
 */
export class AuthPageComponent implements OnInit {

  mode: 'login' | 'register' = 'login';

  loginForm!: FormGroup;

  //
  registerForm!: FormGroup;

  protected readonly SupportedLanguage = SupportedLanguage;

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
   * @param languageService Náš centralizovaný servis pro jazyky
   * @param translateService
   */
  constructor(
    private fb: FormBuilder,
    private auth: AuthManager,
    private router: Router,
    private toastr: ToastrService,
    public languageService: LanguageService,
    private translateService: TranslateService
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

          let loginSuccessText = this.translateService.instant('authPage.notifications.loginSuccess_text');
          // Display success notification.
          this.toastr.success(loginSuccessText);

          // Get the current user.
          let currentUser = this.auth.user()

          // Check if user information is available.
          if (!currentUser) {
            let loginNoUserText = this.translateService.instant('authPage.notifications.loginNoUser_text');
            this.toastr.error(loginNoUserText);
            return;
          }

          // Redirect based on user role.
          AuthRoutingHelper.navigateToRole(this.router, currentUser!.role).then();
        })

        // Handle login failure.
        .catch((e) => {
          console.log('e', e);
          if (e?.status === 401) {
            let loginErrorText = this.translateService.instant('authPage.notifications.loginError_text');
            this.toastr.error(loginErrorText);
          }

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
          let registerSuccessText = this.translateService.instant('authPage.notifications.registerSuccess_text');
          // Display success notification.
          this.toastr.success(registerSuccessText);

          // Get the current user.
          let currentUser = this.auth.user()

          // Redirect based on user role.
          if (!currentUser) {
            let registerNoUserText = this.translateService.instant('authPage.notifications.registerNoUser_text');
            this.toastr.error(registerNoUserText);
          }
          // Redirect based on user role.
          AuthRoutingHelper.navigateToRole(this.router, currentUser!.role).then();
        }).catch(() => {
        // Handle registration failure.
        let registerErrorText = this.translateService.instant('authPage.notifications.registerError_text');
        this.toastr.error(registerErrorText);
      });
    }
  }
}
