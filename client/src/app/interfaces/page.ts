export interface Page<T> {
  content: T[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number; // current page
  numberOfElements: number; // number of elements in this page
  size: number; // page size
  totalElements: number; // total items
  totalPages: number;
}
