import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
// Zde předpokládáme, že tyto pipes jsou dostupné
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {MatIconModule} from '@angular/material/icon'; // Přidáno pro použití ikon

@Component({
  selector: 'app-translator-page',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    DatePipe,
    FilterByStatusPipe,
    LengthPipe,
    MatIconModule // Přidáno
  ],
  templateUrl: './translator-page.component.html',
  styleUrls: ['./translator-page.component.css']
})
export class TranslatorPageComponent implements OnInit {
  protected readonly ProjectStatusDomain = ProjectStatusDomain;

  // MOCK DATA:
  projects: ProjectDomain[] = [
    // Mock ASSIGNED
    { id: '1', status: ProjectStatusDomain.ASSIGNED, originalFileName: 'Article_001.pdf', targetLanguage: 'cs', createdAt: new Date().toISOString() } as ProjectDomain,
    // Mock COMPLETED (Waiting)
    { id: '2', status: ProjectStatusDomain.COMPLETED, originalFileName: 'Brochure_002.docx', targetLanguage: 'de', createdAt: new Date().toISOString() } as ProjectDomain,
    // Mock CLOSED
    { id: '3', status: ProjectStatusDomain.CLOSED, originalFileName: 'Manual_003.txt', targetLanguage: 'fr', createdAt: new Date().toISOString() } as ProjectDomain,
    // Mock APPROVED
    { id: '4', status: ProjectStatusDomain.APPROVED, originalFileName: 'Report_004.pdf', targetLanguage: 'de', createdAt: new Date().toISOString() } as ProjectDomain,
  ];

  isLoading = false;

  // Pohledy pro překladatele
  currentView: 'action' | 'waiting' | 'history' = 'action';

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // V realné aplikaci by zde proběhlo načítání dat
  }

  /**
   * Vrací počet projektů, které jsou přiřazeny a vyžadují akci (upload).
   */
  get actionRequiredCount(): number {
    return this.projects.filter(p => p.status === ProjectStatusDomain.ASSIGNED).length;
  }

  /**
   * Filtruje projekty na základě aktuálního pohledu.
   */
  get projectsForCurrentView(): ProjectDomain[] {
    switch (this.currentView) {
      case 'action':
        return this.projects.filter(p => p.status === ProjectStatusDomain.ASSIGNED);
      case 'waiting':
        return this.projects.filter(p => p.status === ProjectStatusDomain.COMPLETED);
      case 'history':
        return this.projects.filter(p => p.status === ProjectStatusDomain.APPROVED || p.status === ProjectStatusDomain.CLOSED);
      default:
        return [];
    }
  }

  setView(view: 'action' | 'waiting' | 'history'): void {
    this.currentView = view;
  }

  /**
   * Otevře modální okno detailu (předpokládá se logika uploadu pro translatora).
   */
  openProjectDetails(project: ProjectDomain): void {
    // Implementujte vaši dialog opening logic zde
  }
}
