import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {UserDto} from '../services/models/user-dto';


@Injectable({ providedIn: 'root' })
export class UserStateService {
  private currentUserSubject = new BehaviorSubject<UserDto | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor() {}
  setFirstnameAndLastname(user: UserDto): void {
    this.currentUserSubject.next(user);
    sessionStorage.setItem('firstname', user.firstName as string);
    sessionStorage.setItem('lastname', user.lastName as string);
  }
}
