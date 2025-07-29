import {Component, Input} from '@angular/core';
import {CommonModule, DatePipe,NgForOf, NgIf} from '@angular/common';
import {AuditLogsModel} from '../../services/models/audit-logs-model';
import {PdfControllerService} from '../../services/services/pdf-controller.service';
import {saveAs} from 'file-saver';


@Component({
  selector: 'app-logs',
  imports: [
    CommonModule,
    NgForOf,
    DatePipe,
    NgIf
  ],
  templateUrl: './logs.component.html',
  standalone: true,
  styleUrl: './logs.component.scss'
})
export class LogsComponent {

  @Input()
  userEmail:string = '';

  @Input()
  logs: AuditLogsModel[] = [];

  constructor(
    private pdfService: PdfControllerService
  ) {
  }


  downloadLogs() {
    this.pdfService.getUserLogsPdf({ userEmail: this.userEmail })
      .subscribe({
        next: (pdfData: Blob) => {
          saveAs(pdfData,this.userEmail + ' logs.pdf');
        },
        error: err => {
          console.error('Error downloading PDF:', err);
        }
      });
  }
}
