import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [],
  templateUrl: './register.component.html',
  standalone: true,
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  constructor(
    private router:Router
  ) {
  }

  goToLoginPage() {
    this.router.navigate(['login']).then();
  }
}
