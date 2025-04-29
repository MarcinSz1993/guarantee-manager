import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {TokenService} from './token.service';


export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const isLoggedIn = !tokenService.isTokenNotValid() && tokenService.isUserLoggedIn();

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
