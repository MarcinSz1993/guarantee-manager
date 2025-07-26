import { Component, OnInit } from '@angular/core';
import { JsonPipe, NgClass, NgForOf, NgIf, NgStyle } from '@angular/common';
import { GuaranteeControllerService } from '../../services/services/guarantee-controller.service';
import { ApiResponse } from '../../services/models/api-response';
import { GuaranteeHistoryControllerService } from '../../services/services/guarantee-history-controller.service';
import { BaseChartDirective } from 'ng2-charts';
import {NgChartsModule} from 'ng2-charts';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-stats',
  imports: [
    NgForOf,
    NgIf,
    JsonPipe,
    NgChartsModule,
    NgClass,
    NgStyle,
  ],
  templateUrl: './stats.component.html',
  standalone: true,
  styleUrl: './stats.component.scss',
})
export class StatsComponent implements OnInit {
  getMostPopularKindOfProductResponse: ApiResponse = {};
  averageGuaranteeDurationsData: { message: string }[] = [];
  getAllPositiveFeedbacksResponse: ApiResponse = {};
  getAllNegativeFeedbacksResponse: ApiResponse = {};

  guaranteesByStatusData: { status: string; count: string }[] = [];

  constructor(
    private guaranteeService: GuaranteeControllerService,
    private guaranteeHistoryService: GuaranteeHistoryControllerService
  ) {}

  ngOnInit(): void {
    this.getMostPopularKindOfProduct();

    const categories = ['CARS', 'ELECTRONICS', 'OTHER', 'SERVICES', 'CLOTHES'] as const;
    categories.forEach((category) => this.getAverageDurationOfGuarantee(category));

    [true, false].forEach((isPositive) => this.getAllPositiveOrNegativeFeedbacks(isPositive));

    const statuses = ['APPROVED', 'REJECTED', 'EXPIRED', 'PENDING'] as const;
    statuses.forEach((status) => this.getAllGuaranteesByGuaranteeStatus(status));
  }

  getAllPositiveOrNegativeFeedbacks(positiveFeedback: boolean): void {
    this.guaranteeHistoryService
      .getAllPositiveFeedbacks({
        positiveFeedback,
      })
      .subscribe({
        next: (result) => {
          if (positiveFeedback) {
            this.getAllPositiveFeedbacksResponse = result;
          } else {
            this.getAllNegativeFeedbacksResponse = result;
          }
        },
      });
  }

  getAllGuaranteesByGuaranteeStatus(guaranteeStatus: 'ACTIVE' | 'EXPIRED' | 'PENDING' | 'REJECTED' | 'APPROVED'): void {
    this.guaranteeHistoryService
      .getAllByGuaranteeStatus({
        guaranteeStatus,
      })
      .subscribe({
        next: (result) => {
          this.guaranteesByStatusData.push({
            status: guaranteeStatus,
            count: result.message as string,
          });
        },
      });
  }

  getMostPopularKindOfProduct(): void {
    this.guaranteeService.getTheMostPopularKindOfProduct({}).subscribe({
      next: (result) => {
        this.getMostPopularKindOfProductResponse = result;
      },
    });
  }

  getAverageDurationOfGuarantee(kindOfProduct: 'ELECTRONICS' | 'SERVICES' | 'OTHER' | 'CLOTHES' | 'CARS'): void {
    this.guaranteeService
      .getAverageDurationOfGuarantee({
        kindOfProduct,
      })
      .subscribe({
        next: (result) => {
          if (result.message) {
            const formattedMessage = this.formatDurationMessage(result.message);
            this.averageGuaranteeDurationsData.push({ message: formattedMessage });
          }
        },
      });
  }

  formatDurationMessage(message: string): string {
    const parts = message.split('is');
    if (parts.length === 2) {
      const category = parts[0].replace('An average guarantee duration in', '').trim();
      const days = parts[1].replace('days.', '').trim();
      return `Guarantee on  ${category} lasts around ${days} days.`;
    }
    return message;
  }
}
