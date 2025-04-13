import {Component, OnInit} from '@angular/core';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {CollapseDirective} from 'ngx-bootstrap/collapse';
import {RouterLink} from '@angular/router';
import {TokenService} from '../../own_services/token.service';
import {FormsModule} from '@angular/forms';
import {AddGuaranteeComponent} from '../../modals/add-guarantee/add-guarantee.component';
import {ChangePreferenceComponent} from '../../modals/change-preference/change-preference.component';
import {DeleteGuaranteeComponent} from '../../modals/delete-guarantee/delete-guarantee.component';
import {UserStateService} from '../../own_services/user-state-service.service';
import {ChangePasswordComponent} from '../../modals/change-password/change-password.component';
import {DeleteAccountComponent} from '../../modals/delete-account/delete-account.component';

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
    DeleteGuaranteeComponent,
    ChangePasswordComponent,
    DeleteAccountComponent
  ],
  templateUrl: './navbar.component.html',
  standalone: true,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit{
  isAddGuaranteeModalVisible = false;
  isCollapsed: boolean = true;
  isPreferenceModalOpen = false;
  currentPreference = '';

  isDeleteGuaranteeModalOpen = false;
  isChangePasswordModalOpen = false;
  isDeleteAccountModalOpen = false;

  firstName:string = '';
  lastName:string = '';

  constructor(
    public authService:TokenService,
    public userStateService:UserStateService

  ) {

  }

  ngOnInit(): void {
    this.userStateService.currentUser$.subscribe(user => {
      if (user) {
        this.firstName = user.firstName as string;
        this.lastName = user.lastName as string;
      } else {
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
    this.isChangePasswordModalOpen = true;
  }

  onDeleteAccount() {
    this.isDeleteAccountModalOpen = true;
  }

}
