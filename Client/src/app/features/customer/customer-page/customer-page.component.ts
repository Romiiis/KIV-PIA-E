import {Component} from '@angular/core';
import {FilterByStatusPipe} from '@shared/pipes/filter-by-status-pipe';
import {LengthPipe} from '@shared/pipes/length-pipe';
import {NewProjectComponent} from '@features/customer/new-project/new-project.component';
import {NgIf} from '@angular/common';
import {Project} from '@core/models/project.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';

@Component({
  selector: 'app-customer-page',
  templateUrl: './customer-page.component.html',
  imports: [
    FilterByStatusPipe,
    LengthPipe,
    NewProjectComponent,
    NgIf
  ],
  styleUrls: ['./customer-page.component.css']
})
export class CustomerPageComponent {

  showNewProject!: boolean

  openNewProject() {
    console.log('clicked!');
    this.showNewProject = true;
  }

  closeNewProject() {
    this.showNewProject = false;
  }


  projects: Project[] = [
    {
      id: 'p1',
      customerId: 'cust001',
      sourceFileUrl: '/files/documents/contract-en.pdf',
      targetLanguage: 'de',
      status: ProjectStatusDomain.COMPLETED
    },
    {
      id: 'p2',
      customerId: 'cust002',
      translatorId: 'trans001',
      sourceFileUrl: '/files/articles/news-fr.docx',
      targetLanguage: 'en',
      status: ProjectStatusDomain.ASSIGNED
    },
    {
      id: 'p3',
      customerId: 'cust003',
      translatorId: 'trans002',
      sourceFileUrl: '/files/presentations/marketing-es.pptx',
      targetLanguage: 'cs',
      status: ProjectStatusDomain.COMPLETED
    },
    {
      id: 'p4',
      customerId: 'cust004',
      translatorId: 'trans003',
      sourceFileUrl: '/files/manuals/product-it.pdf',
      targetLanguage: 'pl',
      status: ProjectStatusDomain.COMPLETED
    },
    {
      id: 'p5',
      customerId: 'cust005',
      translatorId: 'trans004',
      sourceFileUrl: '/files/letters/letter-ru.txt',
      targetLanguage: 'en',
      status: ProjectStatusDomain.COMPLETED
    }
  ];


  protected readonly ProjectStatusDomain = ProjectStatusDomain;
}
