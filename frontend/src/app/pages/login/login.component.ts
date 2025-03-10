import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UserControllerService} from '../../services/services/user-controller.service';
import {AuthService} from '../../services/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule
  ],
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  isUserLoggedIn = false;
  email: string = '';
  password: string = '';

  constructor(
    private router:Router,
    private userService: UserControllerService,
    private authService: AuthService
  ) {
  }

  onSubmit() {

  }

  goToRegisterPage() {
      this.router.navigate(['register']).then();
  }

  login() {
    this.isUserLoggedIn = true;
    localStorage.setItem('isLoggedIn','true');
    this.router.navigate(['dashboard']).then();
  }
  checkIfUserIsLoggedIn():boolean {
    return localStorage.getItem('isLoggedIn')==='true';
  }
}
