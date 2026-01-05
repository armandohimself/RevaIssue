import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtStorage } from '../../services/jwt-storage';
import { AuthApiService } from '../../features/auth/data-access/auth-api';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const jwtStorage = inject(JwtStorage);
  const router = inject(Router);
  const authApi = inject(AuthApiService);

  // if the token doesnt exist, send to login
  if (!jwtStorage.getToken()) {
    router.navigate(['/login']);
    return false;
  }

  // check if the token is valid
  return authApi.getCurrentUser().pipe(
    map(user => {
      // backend returns, token is valid
      return true;
    }),
    catchError(() => {
      // token is expired
      jwtStorage.clearToken();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
