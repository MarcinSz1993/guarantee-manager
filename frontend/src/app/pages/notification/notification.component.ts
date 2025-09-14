import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf, NgOptimizedImage, SlicePipe} from '@angular/common';
import {
  DashboardNotificationControllerService
} from '../../services/services/dashboard-notification-controller.service';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {UserStateService} from '../../own_services/user-state-service.service';
import {Subscription} from 'rxjs';
import {GuaranteeModalService} from '../../own_services/guarantee-modal.service';
import {NewsArticleControllerService} from '../../services/services/news-article-controller.service';
import {NewsArticleRawApi} from '../../services/models/news-article-raw-api';

@Component({
  selector: 'app-notification',

  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    NgOptimizedImage,
    SlicePipe
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
  newsArticles: NewsArticleRawApi = {};


  constructor(
    private dashboardNotificationService: DashboardNotificationControllerService,
    protected userStateService: UserStateService,
    private guaranteeModalService: GuaranteeModalService,
    private articleGeneratorService: NewsArticleControllerService
  ) {
  }

  ngOnDestroy(): void {
        this.subscription?.unsubscribe();
    }

  ngOnInit(): void {
    this.fetchGeneratedArticles();
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

  fetchGeneratedArticles(){
    this.articleGeneratorService.getNewsArticles({articleParam:'Marcin'})
      .subscribe({next: result=> {
        this.newsArticles = result;
          console.log(this.newsArticles.results)
        }})

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
