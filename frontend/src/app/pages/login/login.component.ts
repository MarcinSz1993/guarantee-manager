import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

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
  email: any;
  password: any;

  constructor(
    private router:Router
  ) {
  }

  onSubmit() {

  }

  goToRegisterPage() {
      this.router.navigate(['register']).then();
  }
}
