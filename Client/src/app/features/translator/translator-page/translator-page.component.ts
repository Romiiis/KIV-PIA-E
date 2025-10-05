import { Component } from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {NewProjectComponent} from '@features/customer/new-project/new-project.component';
import {NgIf} from '@angular/common';
import {Project} from '@core/models/project.model';

@Component({
  selector: 'app-translator-page',
  imports: [
    FilterByStatusPipe,
    LengthPipe,
    NewProjectComponent,
    NgIf
  ],
  templateUrl: './translator-page.component.html',
  styleUrl: './translator-page.component.css'
})
export class TranslatorPageComponent {

  projects: Project[] = [
    {
      id: 'p1',
      customerId: 'cust001',
      sourceFileUrl: '/files/documents/contract-en.pdf',
      targetLanguage: 'de',
      status: 'created'
    },
    {
      id: 'p2',
      customerId: 'cust002',
      translatorId: 'trans001',
      sourceFileUrl: '/files/articles/news-fr.docx',
      targetLanguage: 'en',
      status: 'assigned'
    },
    {
      id: 'p3',
      customerId: 'cust003',
      translatorId: 'trans002',
      sourceFileUrl: '/files/presentations/marketing-es.pptx',
      targetLanguage: 'cs',
      status: 'completed'
    },
    {
      id: 'p4',
      customerId: 'cust004',
      translatorId: 'trans003',
      sourceFileUrl: '/files/manuals/product-it.pdf',
      targetLanguage: 'pl',
      status: 'completed'
    },
    {
      id: 'p5',
      customerId: 'cust005',
      translatorId: 'trans004',
      sourceFileUrl: '/files/letters/letter-ru.txt',
      targetLanguage: 'en',
      status: 'completed'
    }
  ];


}
