import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {UserControllerService} from '../../services/services/user-controller.service';
import {NotificationControllerService} from '../../services/services/notification-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {TokenService} from '../../own_services/token.service';
import {UserDto} from '../../services/models/user-dto';
import {ToastrService} from 'ngx-toastr';
import {UserStateService} from '../../own_services/user-state-service.service';

@Component({
  selector: 'app-change-preference',
  imports: [
    FormsModule
  ],
  templateUrl: './change-preference.component.html',
  standalone: true,
  styleUrl: './change-preference.component.scss'
})
export class ChangePreferenceComponent implements OnInit{
  @Input()
  currentPreference:string = '';
  @Output()
  preferenceChanged = new EventEmitter<string>();
  @Output()
  closeModal = new EventEmitter<void>();

  selectedPreference: 'EMAIL'|'DASHBOARD'|'ALL' = this.currentPreference as 'EMAIL' | 'DASHBOARD' | 'ALL';

  choosePreferenceResponse: ApiResponse = {};
  userDto:UserDto = {};
  userEmail:string = '';
  errorMsg:string = '';

  constructor(
    private notificationService: NotificationControllerService,
    private userService: UserControllerService,
    private tokenService: TokenService,
    private toastrService: ToastrService,
    private userStateService: UserStateService
  ) {
  }

  ngOnInit(): void {
    if(this.currentPreference){
      this.selectedPreference = this.currentPreference as 'EMAIL' | 'DASHBOARD' | 'ALL';
    }
        this.getUserPreference();
    }

    onClose(){
    this.closeModal.emit();
    }

  savePreference(notificationPreference: 'EMAIL'|'DASHBOARD'|'ALL'){
    this.notificationService.chooseNotificationPreference({
      notificationPreference
    }) .subscribe({
      next:(result)=>{
        this.choosePreferenceResponse = result;
        this.userStateService.updatePreference(this.selectedPreference);
        this.toastrService.success(result.message,'',{
          positionClass: 'toast-center-center'
        });
        this.preferenceChanged.emit(this.selectedPreference);
        this.closeModal.emit();
      },error:(err)=> {
        this.errorMsg = err.error.message;
        this.toastrService.error(this.errorMsg,'',{
          positionClass: 'toast-center-center'
        });
      }
    })
  }


  getUserPreference(){
    let username = this.tokenService.getUsername() as string;
    this.userService.getUserByUsername({
      username: username
    }).subscribe({
      next:(result)=> {
        this.userDto = result;
        this.currentPreference = result.notificationPreference as string;
        this.userEmail = this.userDto.notificationPreference as 'EMAIL'|'DASHBOARD'|'ALL';
      },
      error: (err) => {
        this.toastrService.error(err.error.message());
      }
    });
  }

  protected readonly close = close;
}
