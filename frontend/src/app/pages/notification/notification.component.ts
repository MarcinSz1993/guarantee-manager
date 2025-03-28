import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {
  DashboardNotificationControllerService
} from '../../services/services/dashboard-notification-controller.service';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {GuaranteeCardComponent} from '../guarantee-card/guarantee-card.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-notification',
  imports: [
    FormsModule,
    NgForOf,
    GuaranteeCardComponent,
    NgIf,
    RouterLink
  ],
  templateUrl: './notification.component.html',
  standalone: true,
  styleUrl: './notification.component.scss'
})
export class NotificationComponent implements OnInit{
  totalPages:number = 0;
  guaranteeResponse: GuaranteeResponse[] = [];


  constructor(
    private dashboardNotificationService: DashboardNotificationControllerService
  ) {
  }

  ngOnInit(): void {
        this.fetchNotifications();
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
