import {Component, Input, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';
import {TokenService} from '../../own_services/token.service';
import {FormsModule} from '@angular/forms';
import {AddGuaranteeComponent} from '../../modals/add-guarantee/add-guarantee.component';

@Component({
  selector: 'app-navbar',
  imports: [
    NgIf,
    NgOptimizedImage,
    CollapseDirective,
    RouterLink,
    FormsModule,
    AddGuaranteeComponent,
  ],
  templateUrl: './navbar.component.html',
  standalone: true,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit{
  @Input() isModalVisible!:boolean;
  isCollapsed: boolean = true;

  constructor(
    public authService:TokenService
  ) {

  }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
  }

}
