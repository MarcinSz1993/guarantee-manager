import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {ToastrService} from 'ngx-toastr';
import {NgForOf, NgIf} from '@angular/common';

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

  errorMsg:string = '';
   brand: string = '';
   model:string = '';
   notes:string = '';
   kindOfDevice: 'ELECTRONICS' | 'CARS' | 'CLOTHES' | 'SERVICES' | 'OTHER' = 'ELECTRONICS';
   startDate:string = '';
   endDate:string = '';
   selectedFile:File | null = null;

  deviceTypes = ['ELECTRONICS', 'CARS', 'CLOTHES', 'SERVICES', 'OTHER'];

   guarantee: GuaranteeResponse = {};
  isModalVisible: boolean = true;

  constructor(
    private guaranteeService:GuaranteeControllerService,
    private toastr: ToastrService,
  ) {
  }

  addGuarantee() {
    if (!this.selectedFile) {
      alert("Please select a file before submitting!");
      return;
    }

    this.guaranteeService.addGuarantee({
      brand: this.brand,
      model: this.model,
      notes: this.notes,
      kindOfDevice: this.kindOfDevice,
      startDate: this.startDate,
      endDate: this.endDate,
      body: { file: this.selectedFile }
    }).subscribe({
      next: () => {
        this.toastr.success("Guarantee added successfully!",'',{
          positionClass: 'toast-center-center'
        })
        this.closeModal();
      },
      error: (err) => {
        this.errorMsg = err.error.message;
        this.toastr.error(this.errorMsg,'',{
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
  closeModal(){
    this.isModalVisible = false;
    document.body.classList.remove('modal-open');
    document.querySelector('.modal-backdrop')?.remove();
  }

  openModal(){
    this.isModalVisible = true;
  }
}
