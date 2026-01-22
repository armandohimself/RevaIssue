import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../environments/environment';

export const baseURLInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('http')) {
    // Leave relative URLs as-is; Nginx will proxy /api and /issues to backend
    // Only prepend base URL if running in development
    if (!environment.production) {
      const baseUrl = environment.apiUrl; // e.g., 'http://localhost:8081'
      const apiReq = req.clone({ url: `${baseUrl}${req.url}` });
      return next(apiReq);
    }
    // In production, leave relative URLs as-is; Nginx handles them
    return next(req);
  }
  return next(req);
};
