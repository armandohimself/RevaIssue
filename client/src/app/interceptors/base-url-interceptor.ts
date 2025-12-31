import { HttpInterceptorFn } from '@angular/common/http';

export const baseURLInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('Base URL Interceptor triggered!');
  console.log('Request URL:', req.url);
  if (!req.url.startsWith('http')) {
    const baseUrl = 'http://localhost:8081/';
    const apiReq = req.clone({
      url: `${baseUrl}${req.url}`,
    });
    console.log('Modified Request URL:', apiReq.url);
    return next(apiReq);
  }
  console.log('No changes made');
  return next(req);
};
