import {Component, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../services/services/auth.service';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';

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
