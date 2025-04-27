import { Component } from '@angular/core';

@Component({
  selector: 'app-no-guarantees',
  imports: [],
  templateUrl: './no-guarantees.component.html',
  standalone: true,
  styleUrl: './no-guarantees.component.scss'
})
export class NoGuaranteesComponent {
  userName: string = localStorage.getItem("username") as string;
}
