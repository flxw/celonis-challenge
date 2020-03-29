import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimerTaskCreationDialogComponent } from './timer-task-creation-dialog.component';

describe('TimerTaskCreationDialogComponent', () => {
  let component: TimerTaskCreationDialogComponent;
  let fixture: ComponentFixture<TimerTaskCreationDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimerTaskCreationDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimerTaskCreationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
