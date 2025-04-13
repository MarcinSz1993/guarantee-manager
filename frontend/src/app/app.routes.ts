import {Routes} from '@angular/router';
import {HomeComponent} from './pages/home/home.component';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {ActivateAccountComponent} from './pages/activate-account/activate-account.component';
import {NotificationComponent} from './pages/notification/notification.component';
import {StatsComponent} from './pages/stats/stats.component';
import {ArchiveComponent} from './pages/archive/archive.component';
import {NoGuaranteesComponent} from './pages/no-guarantees/no-guarantees.component';
import {authGuard} from './own_services/auth.guard';
import {NavbarComponent} from './pages/navbar/navbar.component';

export const routes: Routes = [
  {
    path: "",
    component: HomeComponent
  },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "register",
    component: RegisterComponent
  },
  {
    path: "dashboard",
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: "activate-account",
    component: ActivateAccountComponent
  },
  {
    path: "notification",
    component: NotificationComponent,
    canActivate: [authGuard]
  },
  {
    path: "stats",
    component: StatsComponent,
    canActivate: [authGuard]
  },
  {
    path: "archive",
    component: ArchiveComponent,
    canActivate: [authGuard]
  },
  {
    path:"no-guarantees",
    component: NoGuaranteesComponent,
    canActivate: [authGuard]
  },
  {
    path: "navbar",
    component: NavbarComponent,
    canActivate: [authGuard]
  }
];
