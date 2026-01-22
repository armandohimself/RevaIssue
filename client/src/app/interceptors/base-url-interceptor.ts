import { HttpInterceptorFn } from '@angular/common/http';

export const baseURLInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('http')) {
    // Leave relative URLs as-is; Nginx will proxy /api and /issues to backend
    return next(req);
  }
  return next(req);
};
