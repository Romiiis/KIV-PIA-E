import { Pipe, PipeTransform } from '@angular/core';
import { Project} from '@core/models/project.model';

@Pipe({
  name: 'filterByStatus'
})
export class FilterByStatusPipe implements PipeTransform {
  transform(projects: Project[], status: Project['status']): Project[] {
    if (!projects) return [];
    if (!status) return projects;
    return projects.filter(p => p.status === status);
  }
}
