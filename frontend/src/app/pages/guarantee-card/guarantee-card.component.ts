import { Component, Input } from '@angular/core';
import {DatePipe, NgClass, NgIf} from '@angular/common';
import {GuaranteeResponse} from '../../services/models/guarantee-response';

@Component({
  selector: 'app-guarantee-card',
  standalone: true,
  templateUrl: './guarantee-card.component.html',
  imports: [
    NgClass,
    NgIf,
    DatePipe
  ],
  styleUrl: './guarantee-card.component.scss'
})
export class GuaranteeCardComponent {
  @Input() guarantee!: GuaranteeResponse;
}
