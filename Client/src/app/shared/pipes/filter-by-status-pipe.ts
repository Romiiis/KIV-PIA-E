import { Pipe, PipeTransform } from '@angular/core';
import { ProjectDomain} from '@core/models/project.model';

@Pipe({
  name: 'filterByStatus'
})
export class FilterByStatusPipe implements PipeTransform {
  transform(projects: ProjectDomain[], status: ProjectDomain['status']): ProjectDomain[] {
    if (!projects) return [];
    if (!status) return projects;
    return projects.filter(p => p.status === status);
  }
}
