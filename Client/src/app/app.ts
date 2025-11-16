import {Component, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {LanguageService} from '@core/services/language.service';
import {registerLocaleData} from '@angular/common';
import localeCs from '@angular/common/locales/cs';
import localeEn from '@angular/common/locales/en';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Client');

  static readonly MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB


  constructor(private langService: LanguageService) {

    registerLocaleData(localeCs);
    registerLocaleData(localeEn);


  }

}
