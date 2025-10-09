import {Component} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {Project} from '@core/models/project.model';

@Component({
  selector: 'app-admin-page.component',
  imports: [
    FilterByStatusPipe,
    LengthPipe
  ],
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.css'
})
export class AdminPageComponent {


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
