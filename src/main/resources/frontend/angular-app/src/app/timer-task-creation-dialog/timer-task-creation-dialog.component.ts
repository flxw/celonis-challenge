import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { TaskCreationPayload } from '../task-creation-payload';

@Component({
  selector: 'app-timer-task-creation-dialog',
  templateUrl: './timer-task-creation-dialog.component.html',
  styleUrls: ['./timer-task-creation-dialog.component.styl']
})
export class TimerTaskCreationDialogComponent {

  public tpl:TaskCreationPayload = new TaskCreationPayload();

  constructor(public dialogRef: MatDialogRef<TimerTaskCreationDialogComponent>) {
    this.tpl.type = "TIMER_TASK";
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  isDataComplete():boolean {
    return this.tpl.x !== undefined && this.tpl.y !== undefined && this.tpl.name !== "";
  }
}
