import { Injectable, signal, WritableSignal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export enum SupportedLanguage {
  EN = 'en',
  CS = 'cs'
}


@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  protected readonly localStorageLangKey = 'lang';

  public activeLanguage: WritableSignal<string>;

  constructor(private translateService: TranslateService) {

    const langs = Object.values(SupportedLanguage);

    this.translateService.addLangs(langs);
    this.translateService.setDefaultLang(langs[0]);

    const savedLang = localStorage.getItem(this.localStorageLangKey);
    const langToUse = (savedLang && this.translateService.getLangs().includes(savedLang)) ? savedLang : langs[0];

    this.activeLanguage = signal(langToUse);

    // Automatizace: Při změně jazyka...
    this.translateService.onLangChange.subscribe(event => {
      // 1. Uložíme do localStorage
      localStorage.setItem(this.localStorageLangKey, event.lang);
      // 2. Aktualizujeme signál (všechny komponenty to hned uvidí)
      this.activeLanguage.set(event.lang);
    });

    // Použijeme jazyk (tím se spustí onLangChange a vše se synchronizuje)
    this.translateService.use(langToUse);
  }


  switchLanguage(lang: SupportedLanguage): void {
    this.translateService.use(lang);
  }

}
