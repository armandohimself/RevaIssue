import { TestBed } from '@angular/core/testing';

import { ProjectsAccessApi } from './projects-access.api';

describe('ProjectsAccessApi', () => {
  let service: ProjectsAccessApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectsAccessApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
