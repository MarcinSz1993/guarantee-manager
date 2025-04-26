import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private tokenKey = 'authToken';
  private username = 'username';

  constructor(
    private router: Router
  ) { }

  setToken(token:string){
    sessionStorage.setItem(this.tokenKey,token);
  }

  isUserLoggedIn(): boolean {
    return sessionStorage.getItem(this.tokenKey) !== null;
  }

  getToken():string | null {
    return sessionStorage.getItem(this.tokenKey) as string;
  }

  getUsername():string | null {
    return sessionStorage.getItem(this.username);
  }

  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.username);
    sessionStorage.removeItem('firstname')
    sessionStorage.removeItem('lastname')
    sessionStorage.removeItem('notificationPreference')
    this.router.navigate(['']).then();
  }

  isTokenNotValid() {
    return !this.isTokenValid();
  }

  private isTokenValid() {
    const token = this.getToken()
    if (!this.getToken()){
      return false;
    }
    const jwtHelper = new JwtHelperService();
    const isTokenExpired = jwtHelper.isTokenExpired(this.getToken());
    if (isTokenExpired){
      sessionStorage.clear();
      return false;
    }
    return true;
  }
}
