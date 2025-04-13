import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {UserDto} from '../services/models/user-dto';


@Injectable({ providedIn: 'root' })
export class UserStateService {
  private currentUserSubject = new BehaviorSubject<UserDto | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();


  constructor() {
    const firstName = sessionStorage.getItem('firstname');
    const lastName = sessionStorage.getItem('lastname');
    const pref = sessionStorage.getItem('notificationPreference');
    const notificationPreference = (pref === 'EMAIL' || pref === 'DASHBOARD' || pref === 'ALL') ? pref : null;

    if (firstName && lastName && notificationPreference) {
      const user: UserDto = {
        firstName,
        lastName,
        notificationPreference
      };
      this.currentUserSubject.next(user);
    }
  }

  setFirstnameAndLastname(user: UserDto): void {
    this.currentUserSubject.next(user);
    sessionStorage.setItem('firstname', user.firstName as string);
    sessionStorage.setItem('lastname', user.lastName as string);
    if (user.notificationPreference) {
      sessionStorage.setItem('notificationPreference', user.notificationPreference);
    }
  }

  getCurrentUser(): UserDto | null {
    return this.currentUserSubject.getValue();
  }

  getUserPreference(): string | null {
    const userPref = this.currentUserSubject.getValue()?.notificationPreference;
    return userPref ?? sessionStorage.getItem('notificationPreference');
  }

  updatePreference(newPreference: 'EMAIL' | 'DASHBOARD' | 'ALL') {
    const user = this.currentUserSubject.getValue();
    if (user) {
      const updatedUser = { ...user, notificationPreference: newPreference };
      this.currentUserSubject.next(updatedUser);
      sessionStorage.setItem('notificationPreference', newPreference);
    }
  }
}
