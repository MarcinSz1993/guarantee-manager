import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private tokenKey = 'authToken';
  private username = 'username';
  private userRoleSubject = new BehaviorSubject<string | null>(null);
  userRole$ = this.userRoleSubject.asObservable();

  constructor(
    private router: Router
  ) {
    this.updateUserRole();
  }

  getUserRole():string | null{
    let token = this.getToken() as string;
    if (!token) return null;
    const jwtHelperService = new JwtHelperService()
    const decodedToken = jwtHelperService.decodeToken(token);
    let userRole = decodedToken.role.toString();
    console.log(userRole);
      return userRole;
  }

  setToken(token:string){
    sessionStorage.setItem(this.tokenKey,token);
    this.updateUserRole();
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
    this.userRoleSubject.next('');
    this.router.navigate(['']).then();
  }

  isTokenNotValid() {
    return !this.isTokenValid();
  }

  private isTokenValid() {
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

  private updateUserRole() {
    const role = this.getUserRole();
    this.userRoleSubject.next(role);
  }
}
