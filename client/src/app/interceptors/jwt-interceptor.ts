import { HttpInterceptorFn } from '@angular/common/http';
import { JwtStorage } from '../services/jwt-storage';
import { inject } from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const jwtStorage = inject(JwtStorage);
  const token = jwtStorage.getToken();
  // console.log('JWT Interceptor triggered!');
  // console.log('JWT Interceptor - Request URL:', req.url);
  // console.log('JWT Interceptor - JWT Token:', token);
  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    // console.log('JWT Interceptor - Modified request headers:', clonedReq.headers.keys());
    // console.log('Request headers:', clonedReq.headers);
    return next(clonedReq);
  }
  // console.log('JWT Interceptor: No changes made');
  return next(req);
};
