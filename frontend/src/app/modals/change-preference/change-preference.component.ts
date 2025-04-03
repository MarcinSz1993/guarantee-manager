import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {UserControllerService} from '../../services/services/user-controller.service';
import {NotificationControllerService} from '../../services/services/notification-controller.service';
import {ApiResponse} from '../../services/models/api-response';
import {TokenService} from '../../own_services/token.service';
import {UserDto} from '../../services/models/user-dto';
import {ToastrService} from 'ngx-toastr';

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
    private toastrService: ToastrService
  ) {
  }

  ngOnInit(): void {
    if(this.currentPreference){
      this.selectedPreference = this.currentPreference as 'EMAIL' | 'DASHBOARD' | 'ALL';
    }
        this.getUserPreference();
    }

  savePreference(notificationPreference: 'EMAIL'|'DASHBOARD'|'ALL'){
    this.notificationService.chooseNotificationPreference({
      notificationPreference
    }) .subscribe({
      next:(result)=>{
        this.choosePreferenceResponse = result;
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
        this.userEmail = this.userDto.notificationPreference as 'EMAIL'|'DASHBOARD'|'ALL';
      },
      error: (err) => {
        this.toastrService.error(err.error.message());
      }
    });
  }
}
