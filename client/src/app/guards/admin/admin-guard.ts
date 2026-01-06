import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtStorage } from '../../services/jwt-storage';
import { AuthApiService } from '../../features/auth/data-access/auth-api';
import { catchError, map, of } from 'rxjs';

export const adminGuard: CanActivateFn = (route, state) => {
  const jwtStorage = inject(JwtStorage);
  const router = inject(Router);
  const authApi = inject(AuthApiService);

  // if token doesnt exist, go to login
  if (!jwtStorage.getToken()) {
    router.navigate(['/login']);
    return false;
  }

  // check if the user is an admin
  return authApi.getCurrentUser().pipe(
    map(user => {
      const isAdmin = user.role === 'ADMIN';

      if (!isAdmin) {
        // user is not an admin, go to user dashboard
        router.navigate(['/user/dashboard']);
      }

      return isAdmin;
    }),
    catchError(() => {
      // token expired or wrong
      jwtStorage.clearToken();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
