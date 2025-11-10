import { Component, Input, OnInit } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ProjectDomain } from '@core/models/project.model';
import { LanguageListService } from '@core/services/languageList.service';
import { MatIconModule } from '@angular/material/icon';
import {MatDialogActions, MatDialogContent} from '@angular/material/dialog';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-project-modal-layout',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatDialogContent, MatDialogActions, TranslatePipe],
  templateUrl: './project-modal-layout.component.html',
  styleUrls: ['./project-modal-layout.component.css']
})
export class ProjectModalLayoutComponent implements OnInit {

  @Input() project!: ProjectDomain;

  public targetLanguageName: string = '';
  public targetLanguageTag: string = '';
  public sourceLanguageTag: string = 'en';

  constructor(private languageService: LanguageListService) {}

  ngOnInit(): void {
    if (!this.project) {
      console.error('ProjectModalLayoutComponent: "project" input is required.');
      return;
    }

    this.targetLanguageName = this.languageService.getLanguageName(this.project.targetLanguage);
    this.targetLanguageTag = this.project.targetLanguage;
  }
}
