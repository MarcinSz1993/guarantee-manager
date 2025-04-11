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

  getCurrentUser(): UserDto | null {
    return this.currentUserSubject.getValue();
  }

  getUserPreference(): string | null {
    return this.currentUserSubject.getValue()?.notificationPreference ?? null;
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
