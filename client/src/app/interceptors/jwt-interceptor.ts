import { HttpInterceptorFn } from '@angular/common/http';
import { JwtStorage } from '../services/jwt-storage';
import { inject } from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const jwtStorage = inject(JwtStorage);
  const token = jwtStorage.getToken();
  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(clonedReq);
  }
  return next(req);
};
