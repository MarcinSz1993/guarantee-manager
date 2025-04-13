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
    localStorage.setItem(this.tokenKey,token);
  }

  isUserLoggedIn(): boolean {
    return localStorage.getItem(this.tokenKey) !== null;
  }

  getToken():string | null {
    return localStorage.getItem(this.tokenKey) as string;
  }

  getUsername():string | null {
    return localStorage.getItem(this.username);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.username);
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
      localStorage.clear();
      return false;
    }
    return true;
  }
}
