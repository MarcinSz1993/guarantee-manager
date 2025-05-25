import {Component, Input} from '@angular/core';
import {CommonModule, DatePipe, JsonPipe, NgForOf, NgIf} from '@angular/common';
import {AuditLogsModel} from '../../services/models/audit-logs-model';

@Component({
  selector: 'app-logs',
  imports: [
    CommonModule,
    NgForOf,
    DatePipe,
    JsonPipe,
    NgIf
  ],
  templateUrl: './logs.component.html',
  standalone: true,
  styleUrl: './logs.component.scss'
})
export class LogsComponent {

  @Input()
  logs: AuditLogsModel[] = [];
}
