import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CreateUserRequest} from '../../services/models/create-user-request';
import {UserControllerService} from '../../services/services/user-controller.service';
import {NgForOf, NgIf} from '@angular/common';
import {ToastrService} from 'ngx-toastr';

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
    private toastr: ToastrService,
  ) {
  }

  goToLoginPage() {
    this.router.navigate(['login']).then();
  }

  register() {
    this.errorMsg = [];
    this.userService.register({
      body: this.registerRequest
    }).subscribe({
      next: (res) => {
        console.log('Error:', res);
        this.toastr.success('Your account has been created. Please check your mailbox and activate the account.',
          '',{
          positionClass: 'toast-center-center'
          });
        this.router.navigate(['login']).then();
        },
      error: (err) => {
        console.log(err);
        if (err.error && err.error.errors) {
          this.errorMsg = Object.values(err.error.errors);
        } else {
          this.errorMsg = [err.error.message];
        }
      }
    });
  }
}
