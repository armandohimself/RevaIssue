import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentTable } from './comment-table';

describe('CommentTable', () => {
  let component: CommentTable;
  let fixture: ComponentFixture<CommentTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommentTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommentTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
