import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {
  DashboardNotificationControllerService
} from '../../services/services/dashboard-notification-controller.service';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {UserStateService} from '../../own_services/user-state-service.service';
import {Subscription} from 'rxjs';
import {GuaranteeModalService} from '../../own_services/guarantee-modal.service';

@Component({
  selector: 'app-notification',

  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './notification.component.html',
  standalone: true,
  styleUrl: './notification.component.scss'
})
export class NotificationComponent implements OnInit, OnDestroy{
  private subscription: Subscription | undefined;
  totalPages:number = 0;
  guaranteeResponse: GuaranteeResponse[] = [];
  userPreference: string = '';


  constructor(
    private dashboardNotificationService: DashboardNotificationControllerService,
    protected userStateService: UserStateService,
    private guaranteeModalService: GuaranteeModalService
  ) {
  }

  ngOnDestroy(): void {
        this.subscription?.unsubscribe();
    }

  ngOnInit(): void {
    this.fetchNotifications();
    this.userPreference = this.userStateService.getUserPreference() as string;
    console.log('Preferencja: '+this.userPreference);
    this.subscription = this.guaranteeModalService.guaranteeDeleted$
      .subscribe(()=> {
        this.fetchNotifications()
      });
    this.subscription = this.guaranteeModalService.guaranteeAdded$
      .subscribe(()=>{
        this.fetchNotifications()
      });
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
