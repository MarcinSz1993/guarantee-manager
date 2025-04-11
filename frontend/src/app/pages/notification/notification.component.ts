import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {
  DashboardNotificationControllerService
} from '../../services/services/dashboard-notification-controller.service';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {GuaranteeCardComponent} from '../guarantee-card/guarantee-card.component';
import {RouterLink} from '@angular/router';
import {UserStateService} from '../../own_services/user-state-service.service';

@Component({
  selector: 'app-notification',
  imports: [
    FormsModule,
    NgForOf,
    GuaranteeCardComponent,
    NgIf,
    RouterLink,
    AsyncPipe
  ],
  templateUrl: './notification.component.html',
  standalone: true,
  styleUrl: './notification.component.scss'
})
export class NotificationComponent implements OnInit{
  totalPages:number = 0;
  guaranteeResponse: GuaranteeResponse[] = [];
  userPreference: string = '';



  constructor(
    private dashboardNotificationService: DashboardNotificationControllerService,
    protected userStateService: UserStateService
  ) {
  }

  ngOnInit(): void {
    this.fetchNotifications();
    this.userPreference = this.userStateService.getUserPreference() as string;
    console.log('Preferencja: '+this.userPreference);
  }


  fetchNotifications() {
    this.dashboardNotificationService.fetchGuaranteesExpiresIn7Days()
      .subscribe({
        next:(response)=>{
          this.guaranteeResponse = response.content ?? [];
          this.totalPages = response.totalPages as number;
        }
      })
  }
}
