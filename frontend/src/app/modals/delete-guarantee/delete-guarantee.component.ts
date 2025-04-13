import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {PageGuaranteeResponse} from '../../services/models/page-guarantee-response';
import {NgForOf} from '@angular/common';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';
import {GuaranteeModalService} from '../../own_services/guarantee-modal.service';

@Component({
  selector: 'app-delete-guarantee',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgForOf
  ],
  templateUrl: './delete-guarantee.component.html',
  standalone: true,
  styleUrl: './delete-guarantee.component.scss'
})
export class DeleteGuaranteeComponent implements OnInit{
  @Output()
  closeModal = new EventEmitter<void>();
  selectedGuaranteeIdToDelete:number = 0;
  allGuarantees:PageGuaranteeResponse = {};
  deleteGuaranteeResponse:ApiResponse = {};
  errorMsg:string = '';

  constructor(
    private guaranteeService:GuaranteeControllerService,
    private toastrService: ToastrService,
    private deleteGuaranteeModalService: GuaranteeModalService
  ) {
  }

  ngOnInit(): void {
        this.getAllGuarantees();
    }

  getAllGuarantees(){
    this.guaranteeService.getAllUserGuarantees({
      size:10,
      page:0
    }).subscribe({
      next:(result)=>{
        this.allGuarantees = result
      }
    });
  }

  onClose(){
    this.closeModal.emit();
  }

  deleteGuarantee(){
    const confirmDelete = window.confirm("Are you sure?");
    if (!confirmDelete){
      return;
    }
    this.guaranteeService.deleteGuarantee({
      guaranteeId:this.selectedGuaranteeIdToDelete
    }).subscribe({
      next:(result)=>{
        this.deleteGuaranteeResponse = result;
        console.log("Id "+ this.selectedGuaranteeIdToDelete);
        console.log("Response "+ this.deleteGuaranteeResponse.message);
        this.deleteGuaranteeModalService.notifyGuaranteeDeleted();
        this.onClose();
        this.toastrService.success(result.message,'',{
          positionClass: 'toast-center-center'
        });
      },
      error: (err)=>{
        this.errorMsg = err.error.message;
        this.toastrService.error(this.errorMsg,'',{
          positionClass: 'toast-center-center'
        })
      }
    });
  }
}
