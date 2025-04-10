import {Component, EventEmitter, Output} from '@angular/core';
import {UserControllerService} from '../../services/services/user-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';
import {TokenService} from '../../own_services/token.service';

@Component({
  selector: 'app-delete-account',
  imports: [],
  templateUrl: './delete-account.component.html',
  standalone: true,
  styleUrl: './delete-account.component.scss'
})
export class DeleteAccountComponent {
  @Output()
  closeModal = new EventEmitter<void>();
  @Output()
  confirmDelete = new EventEmitter<void>();

  deleteUserResponse:ApiResponse = {};
  errorMsg:string = '';

  constructor(
    private userService:UserControllerService,
    private toastrService:ToastrService,
    private tokenService:TokenService
  ) {
  }

  onClose(){
    this.closeModal.emit();
  }

  deleteAccount(){
    this.userService.deleteUser()
      .subscribe({
        next:(result)=> {
          this.deleteUserResponse = result;
          this.toastrService.success(result.message,'',{
            positionClass: 'toast-center-center'
          });
          this.tokenService.logout();
        },
        error:(err)=>{
          this.errorMsg = err.error.message;
          this.toastrService.error(this.errorMsg,'',{
            positionClass: 'toast-center-center'
          })
        }
      })
    this.confirmDelete.emit();
    this.onClose();
  }
}
