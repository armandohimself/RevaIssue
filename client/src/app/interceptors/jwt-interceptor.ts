import { HttpInterceptorFn } from '@angular/common/http';
import { JwtStorage } from '../services/jwt-storage';
import { inject } from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const jwtStorage = inject(JwtStorage);
  const token = jwtStorage.getToken();
  console.log('Interceptor triggered!');
  console.log('Request URL:', req.url);
  console.log('JWT Token:', token);
  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log('Modified request headers:', clonedReq.headers.keys());
    console.log('Request headers:', clonedReq.headers);
    return next(clonedReq);
  }
  return next(req);
};
