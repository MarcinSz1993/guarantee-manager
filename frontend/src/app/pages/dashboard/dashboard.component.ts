import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import { GuaranteeControllerService } from '../../services/services/guarantee-controller.service';
import { PageGuaranteeResponse} from '../../services/models/page-guarantee-response';
import { ApiModule } from '../../services/api.module';
import { GuaranteeCardComponent } from '../guarantee-card/guarantee-card.component';
import {GuaranteeResponse} from '../../services/models/guarantee-response';
import {NoGuaranteesComponent} from '../no-guarantees/no-guarantees.component';
import {AddGuaranteeComponent} from '../../modals/add-guarantee/add-guarantee.component';
import {GuaranteeModalService} from '../../own_services/guarantee-modal.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgForOf,
    ApiModule,
    GuaranteeCardComponent,
    NgIf,
    NoGuaranteesComponent,
    AddGuaranteeComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  hasTriedToLoadPrevious = false;
  hasGuarantee:boolean = true;
  isAddGuaranteeModalVisible = false;
  subscription: Subscription | undefined;

  userName: string = localStorage.getItem("username") as string;
  errorMsg: string = '';
  allUserGuarantees: GuaranteeResponse[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 4;
  totalPages: number = 1;

  constructor(private guaranteeService: GuaranteeControllerService,
              private guaranteeModalService: GuaranteeModalService)
  {}

  ngOnDestroy(): void {
        this.subscription?.unsubscribe();
    }

  ngOnInit(): void {
    this.getAllUserGuarantees();
    this.subscription = this.guaranteeModalService.guaranteeAdded$
      .subscribe(()=>{
        this.getAllUserGuarantees();
      });
    this.subscription = this.guaranteeModalService.guaranteeDeleted$
      .subscribe(()=>{
        this.getAllUserGuarantees();
      })
  }


  getAllUserGuarantees() {
    this.guaranteeService.getAllUserGuarantees({
      page: this.currentPage - 1,
      size: this.itemsPerPage
    })
      .subscribe({
        next: (response: PageGuaranteeResponse) => {
          this.allUserGuarantees = response.content ?? [];
          this.totalPages = response.totalPages as number;

          if (response.content && response.content.length > 0) {
            this.hasGuarantee = true;
          } else {
            this.hasGuarantee = false;
          }
          if (response.content?.length===0 && response.first === false && !this.hasTriedToLoadPrevious){
            if (this.currentPage > 1) {
              this.currentPage--;
              this.getAllUserGuarantees();
              return;
            }
          }
          this.hasTriedToLoadPrevious = false;
        },
        error: (err) => {
          this.errorMsg = err.error.message;
        }
      });
  }

  nextPage(){
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.getAllUserGuarantees();
    }
  }

  previousPage(){
    if (this.currentPage > 1){
      this.currentPage--;
      this.getAllUserGuarantees();
    }
  }

  closeAddGuaranteeModal(){
    this.isAddGuaranteeModalVisible = false;
  }
}
