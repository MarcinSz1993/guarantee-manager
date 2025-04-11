import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ChangePasswordRequest} from '../../services/models/change-password-request';
import {PasswordControllerService} from '../../services/services/password-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-change-password',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    NgIf
  ],
  templateUrl: './change-password.component.html',
  standalone: true,
  styleUrl: './change-password.component.scss'
})
export class ChangePasswordComponent {
  @Input()
  isModalVisible!:boolean;
  @Output()
  closeModal = new EventEmitter<void>();

  changePasswordRequest: ChangePasswordRequest = {};
  changePasswordResponse:ApiResponse = {};
  errorMsg:string = '';

  constructor(
    private passwordService: PasswordControllerService,
    private toastrService: ToastrService
  ) {
  }

  onClose(){
    this.closeModal.emit();
  }

  changePassword(){
    this.passwordService.changePassword({
      body: this.changePasswordRequest
    }).subscribe({
      next:(result)=>{
        this.changePasswordResponse = result;
        this.toastrService.success(result.message,'',{
          positionClass: 'toast-center-center'
        });
        this.onClose();
      },
      error:(err)=>{
        const errors = err.error.errors;
        if (errors){
          const firstErrorMsg = Object.values(errors)[0] as string;
          this.toastrService.error(firstErrorMsg, '', {
            positionClass: 'toast-center-center'
          });
          return;
        }

        this.errorMsg = err.error.message;
        this.toastrService.error(this.errorMsg,'',{
          positionClass: 'toast-center-center'
        })
      }
    });
  }
}
