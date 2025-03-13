import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CreateUserRequest} from '../../services/models/create-user-request';
import {UserControllerService} from '../../services/services/user-controller.service';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './register.component.html',
  standalone: true,
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  errorMsg: Array<string> = [];
  confirmPassword:string = '';
  registerRequest: CreateUserRequest = {
    firstName: '',
    lastName:  '',
    username: '',
    password: '',
    email: ''
  };




  constructor(
    private router:Router,
    private userService: UserControllerService,
  ) {
  }

  goToLoginPage() {
    this.router.navigate(['login']).then();
  }

  register(){
      this.errorMsg = [];
      this.userService.register({
        body: this.registerRequest
      }).subscribe({
        next: (res) => {
          alert("Your account has been created. Please check your mailbox and activate the account.")
        },
        error: (err)=> {
          console.log(err);
        }
      });
  }
}
