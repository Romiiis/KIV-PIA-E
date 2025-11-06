// project-detail-modal.component.ts

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectStatusDomain } from '@core/models/projectStatus.model';

@Component({
  selector: 'app-project-detail-modal',
  standalone: true,
  imports: [CommonModule], // CommonModule is needed for @if and the 'date' pipe
  templateUrl: './project-detail-modal.component.html',
  styleUrls: ['./project-detail-modal.component.css']
})
export class ProjectDetailModalComponent {

  /**
   * The project whose details are to be displayed.
   * Receives data from the parent component.
   */
  @Input() project!: ProjectDomain;

  /**
   * Event emitted when the modal should be closed.
   */
  @Output() close = new EventEmitter<void>();

  // Expose the enum to the template for status-based styling
  protected readonly ProjectStatusDomain = ProjectStatusDomain;

  /**
   * Closes the modal by emitting the 'close' event.
   */
  onClose(): void {
    this.close.emit();
  }
}
