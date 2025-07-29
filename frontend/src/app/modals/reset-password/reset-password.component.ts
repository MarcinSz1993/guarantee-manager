import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ResetPasswordRequest} from '../../services/models/reset-password-request';
import {PasswordControllerService} from '../../services/services/password-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-reset-password',
  imports: [
    FormsModule
  ],
  templateUrl: './reset-password.component.html',
  standalone: true,
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
  @Output()
  closeResetPasswordModal = new EventEmitter<void>();
  isSubmitting = false;
  //todo pododawałem isSubmitting przy wysyłanie błędnego request o zmianie hasła.

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
    this.isSubmitting = true;
      this.passwordService.sendResetPasswordEmail({
        body:this.resetPasswordRequest
      }).subscribe({
        next:(result)=>{
          this.resetPasswordResponse = result
          this.onClose();
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
            this.isSubmitting = false;
            return;
          }
          this.errorMsg = err.error.message;
          this.toastrService.error(this.errorMsg,'',{
            positionClass: 'toast-center-center'
          })
          this.isSubmitting = false;
        }
      });
  }
}
//todo rozwiązanie to dodać metodę onClose() po poprawnej odpowiedzi na requesta o reset maila.
