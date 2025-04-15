import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {ResetPasswordRequest} from '../../services/models/reset-password-request';
import {PasswordControllerService} from '../../services/services/password-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-reset-password',
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './reset-password.component.html',
  standalone: true,
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
  @Output()
  closeResetPasswordModal = new EventEmitter<void>();

  resetPasswordRequest:ResetPasswordRequest = {
    email:''
  }
  resetPasswordResponse:ApiResponse = {};
  errorMsg = '';

  constructor(
    private passwordService:PasswordControllerService,
    private toastrService: ToastrService
  ) {
  }

  onClose() {
    this.closeResetPasswordModal.emit();

  }

  resetPassword() {
      this.passwordService.sendResetPasswordEmail({
        body:this.resetPasswordRequest
      }).subscribe({
        next:(result)=>{
          this.resetPasswordResponse = result
          this.toastrService.success(this.resetPasswordResponse.message,'',{
            positionClass: 'toast-center-center'
          })
        },
        error:(err)=>{
          if(err.error.errors){
            this.errorMsg = Object.values(err.error.errors)[0] as string;
            this.toastrService.error(this.errorMsg,'',{
              positionClass: 'toast-center-center'
            })
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
