import { Component } from '@angular/core';
import {NgForOf} from '@angular/common';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {PageGuaranteeResponse} from '../../services/models/page-guarantee-response';

@Component({
  selector: 'app-dashboard',
  imports: [
    NgForOf
  ],
  templateUrl: './dashboard.component.html',
  standalone: true,
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  userName: string = 'Marcin!';
  errorMsg:string = '';
  allUserGuarantees: PageGuaranteeResponse = {};

  constructor(
    private guaranteeService:GuaranteeControllerService
  ) {
  }

  getAllUserGuarantees() {
    this.guaranteeService.getAllUserGuarantees()
      .subscribe({
        next: (userGuarantees) => {
          this.allUserGuarantees = userGuarantees;
        },
        error: (err) => {
          this.errorMsg = err.message;
        }
      });
  }


}

//todo dokończyć dashboard czyli miejsce gdzie będą się wyświetlały aktualne gwarancje
//todo dodać interceptor, aby nie trzeba było się logować do wysłania każdego żądania
