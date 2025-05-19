import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './pages/navbar/navbar.component';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,NavbarComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  constructor(
    private titleService: Title
  ) {
  }
  ngOnInit(): void {
    this.titleService.setTitle(this.title);
  }
  title = 'Guarantee Manager';
}
