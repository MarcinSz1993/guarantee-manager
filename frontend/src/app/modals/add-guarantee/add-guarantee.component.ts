import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {ToastrService} from 'ngx-toastr';
import {NgForOf, NgIf} from '@angular/common';
import {AddGuaranteeRequest} from '../../services/models/add-guarantee-request';
import {GuaranteeModalService} from '../../own_services/guarantee-modal.service';
import {finalize} from 'rxjs';

@Component({
  selector: 'app-add-guarantee',
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './add-guarantee.component.html',
  standalone: true,
  styleUrl: './add-guarantee.component.scss'
})
export class AddGuaranteeComponent {
  @Output()
  closeModal = new EventEmitter<void>();
  errorMsg = '';
  productTypes = ['ELECTRONICS', 'CARS', 'CLOTHES', 'SERVICES', 'OTHER'];
  guarantee: GuaranteeResponse = {};
  selectedFile:File | null = null;
  addGuaranteeRequest: AddGuaranteeRequest = {
    brand: '',
    model: '',
    notes: '',
    kindOfProduct: 'ELECTRONICS',
    startDate: '',
    endDate: '',
  };
  isSubmitting = false;


  constructor(
    private guaranteeService:GuaranteeControllerService,
    private toastrService: ToastrService,
    private guaranteeModalService: GuaranteeModalService
  ) {
  }

  addGuarantee() {
    if (this.isSubmitting){
      return;
    }
    this.isSubmitting = true;
    if (!this.selectedFile) {
      alert("Please select a file before submitting!");
      this.isSubmitting = false;
      //todo zmieniamy isSubmitting na false po nie dodaniu załącznika.
      return;
    }

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(this.addGuaranteeRequest)], { type: 'application/json' }));

    if (this.selectedFile) {
      formData.append('file', this.selectedFile, this.selectedFile.name);
    }

    this.guaranteeService.addGuarantee({
      body:{
        file: this.selectedFile,
        data: this.addGuaranteeRequest
      }
    }).pipe(
      finalize(()=> this.isSubmitting =false)
    ).subscribe({
      next: () => {
        this.toastrService.success("Guarantee added successfully!", '', {
          positionClass: 'toast-center-center'
        });
        this.guaranteeModalService.notifyGuaranteeAdded();
        this.onClose();
      },
      error: (err) => {
        const validationErrors = err.error?.errors;
        if(validationErrors){
          this.errorMsg = Object.values(validationErrors)[0] as string;
          this.toastrService.error(this.errorMsg,'',{
            positionClass: 'toast-center-center'
          });
          return;
        }
        this.errorMsg = err.error.message;
        this.toastrService.error(this.errorMsg, '', {
          positionClass: 'toast-center-center'
        });
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  onClose() {
    this.closeModal.emit();
  }
}
