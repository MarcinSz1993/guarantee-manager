import { Component } from '@angular/core';
import {CodeInputModule} from 'angular-code-input';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {UserControllerService} from '../../services/services/user-controller.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-activate-account',
  imports: [
    CodeInputModule,
    NgIf,
    FormsModule
  ],
  templateUrl: './activate-account.component.html',
  standalone: true,
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {
  submitted: boolean = false;
  isOkay: boolean = true;
  message: string = '';

  constructor(
    private router:Router,
    private userService: UserControllerService
  ) {
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);

  }

  redirectToLogin() {
    this.router.navigate(['/login']).then();
  }

  private confirmAccount(userActivationToken: string) {
    this.userService.activateUser({
      userActivationToken
    }).subscribe({
      next:()=>{
        this.message = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.submitted = true;
      },
      error:() => {
        this.message = 'Token has been expired or invalid';
        this.submitted = true;
        this.isOkay = false;

      }
    })
  }
}
