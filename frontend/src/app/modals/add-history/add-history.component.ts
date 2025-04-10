import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {PageGuaranteeResponse} from '../../services/models/page-guarantee-response';
import {NgForOf, NgIf} from '@angular/common';
import {CreateGuaranteeHistoryRequest} from '../../services/models/create-guarantee-history-request';
import {GuaranteeHistoryControllerService} from '../../services/services/guarantee-history-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-add-history',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './add-history.component.html',
  standalone: true,
  styleUrl: './add-history.component.scss'
})
export class AddHistoryComponent implements OnInit{
  @Output()
  closeModalEvent = new EventEmitter<void>();
  @Output()
  addedGuaranteeHistory = new EventEmitter<void>();
  errorMsg:string = '';
  allGuarantees:PageGuaranteeResponse = {};
  createGuaranteeHistoryRequest: CreateGuaranteeHistoryRequest = {
    guaranteeId: 0,
    guaranteeStatus: 'PENDING',
    notes: '',
    positiveFeedback: false
  };
  addGuaranteeHistoryResponse: ApiResponse = {};

  constructor(
    private guaranteeService: GuaranteeControllerService,
    private guaranteeHistoryService: GuaranteeHistoryControllerService,
    private toastrService: ToastrService
  ) {
  }

  ngOnInit(): void {
        this.fetchGuarantees();
    }

  fetchGuarantees(){
    this.guaranteeService.getAllUserGuarantees({
    }).subscribe({
      next:(result)=>{
        this.allGuarantees = result;
      },
      error:(err)=>{
        this.errorMsg = err.error.message;
      }
    })
  }

  addGuaranteeHistory() {
    this.guaranteeHistoryService.addGuaranteeChange({
      createGuaranteeHistoryRequest: this.createGuaranteeHistoryRequest
    }).subscribe({
      next: (result) => {
        this.addGuaranteeHistoryResponse = result;
        this.addedGuaranteeHistory.emit();
        console.log(result.message);
        this.closeModal();
        this.toastrService.success(result.message, '', {
          positionClass: 'toast-center-center'
        });
      },
      error: (err) => {
        const errors = err.error?.errors;
        if (errors) {
          this.errorMsg = Object.values(errors)[0] as string;
          this.toastrService.error(this.errorMsg,'',{
            positionClass: 'toast-center-center'
          });
        }
      }
    });
  }
  closeModal(){
    this.closeModalEvent.emit();
  }
}
