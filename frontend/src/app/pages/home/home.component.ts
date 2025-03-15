import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [

  ],
  templateUrl: './home.component.html',
  standalone: true,
  styleUrl: './home.component.scss'
})
export class HomeComponent{
  constructor(
    private router:Router
  ) {
  }
  goToLoginPage() {
    this.router.navigate(['login']).then();
  }
}
