import {Component, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';
import {AuthService} from '../../own_services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [
    NgIf,
    NgOptimizedImage,
    CollapseDirective,
    RouterLink,
  ],
  templateUrl: './navbar.component.html',
  standalone: true,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit{
  isUserLoggedIn:boolean = false;
  isCollapsed: boolean = true;

  constructor(
    public authService:AuthService
  ) {

  }

  ngOnInit(): void {
  }

  onCollapse() {

  }
}
