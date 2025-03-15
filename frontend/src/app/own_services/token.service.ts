import { Injectable } from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private tokenKey = 'authToken';

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

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['']).then();
  }
}
