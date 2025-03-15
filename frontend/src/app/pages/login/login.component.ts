import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UserControllerService} from '../../services/services/user-controller.service';
import {AuthService} from '../../own_services/auth.service';
import {LoginRequest} from '../../services/models/login-request';
import {AuthenticationResponse} from '../../services/models/authentication-response';
import {NgForOf, NgIf} from '@angular/common';



@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginRequest: LoginRequest = {
    userEmail: '',
    password:''
  }
  authenticationResponse: AuthenticationResponse = {};
  errorMsg:string = '';

  constructor(
    private router:Router,
    private userService: UserControllerService,
    private authService: AuthService
  ) {
  }

  goToRegisterPage() {
      this.router.navigate(['register']).then();
  }

  login() {
    this.errorMsg = '';
    this.userService.login({
      body: this.loginRequest
    }).subscribe({
      next:(loginResponse) =>{
        this.authenticationResponse = loginResponse
        this.authService.setToken(loginResponse.token as string);
        this.router.navigate(['dashboard']).then();
        console.log(this.authenticationResponse);
        console.log(loginResponse);
      },
        error: (err) => {
        this.errorMsg = err.error.message;
        }
    });

  }
}
