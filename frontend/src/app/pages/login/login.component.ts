import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UserControllerService} from '../../services/services/user-controller.service';
import {TokenService} from '../../own_services/token.service';
import {LoginRequest} from '../../services/models/login-request';
import {AuthenticationResponse} from '../../services/models/authentication-response';
import {NgIf} from '@angular/common';
import {UserDto} from '../../services/models/user-dto';
import {UserStateService} from '../../own_services/user-state-service.service';
import {ResetPasswordComponent} from '../../modals/reset-password/reset-password.component';



@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgIf,
    ResetPasswordComponent
  ],
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  isResetPasswordModalVisible = false;

  userDto: UserDto = {};
  loginRequest: LoginRequest = {
    userEmail: '',
    password:''
  };
  authenticationResponse: AuthenticationResponse = {};
  errorMsg:string = '';

  constructor(
    private router:Router,
    private userService: UserControllerService,
    private authService: TokenService,
    private userStateService: UserStateService
  ) {
  }

  goToRegisterPage() {
      this.router.navigate(['register']).then();
  }

  login() {
    this.errorMsg = '';

    this.userService.login({ body: this.loginRequest }).subscribe({
      next: (loginResponse) => {
        this.authenticationResponse = loginResponse;
        this.authService.setToken(loginResponse.token as string);

        this.userService.getUserByEmail({ email: this.loginRequest.userEmail })
          .subscribe({
            next: (response) => {
              this.userDto = response;
              this.userStateService.setFirstnameAndLastname(this.userDto);
              sessionStorage.setItem("username",this.userDto.userName as string);
              this.router.navigate(['dashboard']).then();
            },
            error: (err) => {
              this.errorMsg = err.error.message;
            }
          });
      },
      error: (err) => {
        this.errorMsg = err.error.message;
      }
    });
  }

  openResetPasswordModal() {
    this.isResetPasswordModalVisible = true;
  }
  closeResetPasswordModal(){
    this.isResetPasswordModalVisible = false;
  }
}
