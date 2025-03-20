import {Component, OnInit} from '@angular/core';
import {NgForOf} from '@angular/common';
import {GuaranteeControllerService} from '../../services/services/guarantee-controller.service';
import {PageGuaranteeResponse} from '../../services/models/page-guarantee-response';
import {getAllUserGuarantees} from '../../services/fn/guarantee-controller/get-all-user-guarantees';

@Component({
  selector: 'app-dashboard',
  imports: [
    NgForOf
  ],
  templateUrl: './dashboard.component.html',
  standalone: true,
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit{
  userName: string = localStorage.getItem("username") as string;
  errorMsg:string = '';
  allUserGuarantees: PageGuaranteeResponse = {};

  constructor(
    private guaranteeService:GuaranteeControllerService
  ) {
  }

  ngOnInit(): void {
        this.getAllUserGuarantees();
    }


  getAllUserGuarantees() {
    this.guaranteeService.getAllUserGuarantees()
      .subscribe({
        next: (userGuarantees) => {
          this.allUserGuarantees = userGuarantees;
          console.log(this.allUserGuarantees);
        },
        error: (err) => {
          this.errorMsg = err.error.message;
        }
      });
  }


}

//todo dokończyć dashboard czyli miejsce gdzie będą się wyświetlały aktualne gwarancje
//todo dodać interceptor, aby nie trzeba było się logować do wysłania każdego żądania
