import {Component, Input, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';
import {TokenService} from '../../own_services/token.service';
import {FormsModule} from '@angular/forms';
import {AddGuaranteeComponent} from '../../modals/add-guarantee/add-guarantee.component';
import {ChangePreferenceComponent} from '../../modals/change-preference/change-preference.component';
import {DeleteGuaranteeComponent} from '../../modals/delete-guarantee/delete-guarantee.component';
import {UserStateService} from '../../own_services/user-state-service.service';

@Component({
  selector: 'app-navbar',
  imports: [
    NgIf,
    NgOptimizedImage,
    CollapseDirective,
    RouterLink,
    FormsModule,
    AddGuaranteeComponent,
    ChangePreferenceComponent,
    DeleteGuaranteeComponent
  ],
  templateUrl: './navbar.component.html',
  standalone: true,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit{
  @Input()
  isModalVisible!:boolean;
  isCollapsed: boolean = true;
  isPreferenceModalOpen = false;
  currentPreference = 'EMAIL'

  isDeleteGuaranteeModalOpen = false;

  firstName:string = '';
  lastName:string = '';

  constructor(
    public authService:TokenService,
    public userStateService:UserStateService

  ) {

  }

  ngOnInit(): void {
// Subskrybuj dane użytkownika w serwisie UserStateService
    this.userStateService.currentUser$.subscribe(user => {
      if (user) {
        this.firstName = user.firstName as string;
        this.lastName = user.lastName as string;
      } else {
        // Możesz także odczytać dane z sessionStorage, jeśli użytkownik nie jest zalogowany
        this.firstName = sessionStorage.getItem('firstname') as string;
        this.lastName = sessionStorage.getItem('lastname') as string;
      }
    });

  }

  logout() {
    this.authService.logout();
  }

  onDeleteGuarantee() {
    this.isDeleteGuaranteeModalOpen = true;
  }

  onChangePreferences() {
    this.isPreferenceModalOpen = true;

  }

  updatePreference(newPreference:string){
    this.currentPreference = newPreference;
  }

  onChangePassword() {

  }

  onDeleteAccount() {

  }

}
