import { HttpInterceptorFn } from '@angular/common/http';

export const baseURLInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('http')) {
    const baseUrl = 'http://localhost:8081/';
    const apiReq = req.clone({
      url: `${baseUrl}${req.url}`,
    });
    return next(apiReq);
  }
  return next(req);
};
