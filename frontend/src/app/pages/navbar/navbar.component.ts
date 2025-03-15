import {Component, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';
import {TokenService} from '../../own_services/token.service';

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
  isCollapsed: boolean = true;

  constructor(
    public authService:TokenService
  ) {

  }

  ngOnInit(): void {
  }

  onCollapse() {

  }

  logout() {
    this.authService.logout();
  }
}
