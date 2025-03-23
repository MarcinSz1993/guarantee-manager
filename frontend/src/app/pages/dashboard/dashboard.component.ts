import { Component, OnInit } from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import { GuaranteeControllerService } from '../../services/services/guarantee-controller.service';
import { PageGuaranteeResponse} from '../../services/models/page-guarantee-response';
import { ApiModule } from '../../services/api.module';
import { GuaranteeCardComponent } from '../guarantee-card/guarantee-card.component';
import {GuaranteeResponse} from '../../services/models/guarantee-response';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgForOf,
    ApiModule,
    GuaranteeCardComponent,
    NgIf
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  userName: string = localStorage.getItem("username") as string;
  errorMsg: string = '';
  allUserGuarantees: GuaranteeResponse[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 4;
  totalPages: number = 1;

  constructor(private guaranteeService: GuaranteeControllerService) {}

  ngOnInit(): void {
    this.getAllUserGuarantees();
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

  prevPage(){
    if (this.currentPage > 1){
      this.currentPage--;
      this.getAllUserGuarantees();
    }
  }
}
