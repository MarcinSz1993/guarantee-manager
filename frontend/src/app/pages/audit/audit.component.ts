import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuditControllerService} from '../../services/services/audit-controller.service';
import {AuditResponse} from '../../services/models/audit-response';
import {NgIf} from '@angular/common';
import {LogsComponent} from '../logs/logs.component';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-audit',
  imports: [
    FormsModule,
    NgIf,
    LogsComponent
  ],
  templateUrl: './audit.component.html',
  standalone: true,
  styleUrl: './audit.component.scss'
})
export class AuditComponent {
   userEmail = '';
   auditResponse:AuditResponse = {};
   errorMsg = '';

   constructor(
     private auditService:AuditControllerService,
     private toastrService: ToastrService
   ) {
   }

  getLogs() {
     this.auditService.getAudit({email: this.userEmail,size: 100})
       .subscribe({
         next: (result => {
           this.auditResponse = result;
         }),error:(err)=>{
           this.errorMsg = err.error.message;
           this.toastrService.error(this.errorMsg,'',{
             positionClass: 'toast-center-center'
           });
         }

  });
   }}

