import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TaskCreationPayload } from '../task-creation-payload';
import { SessionStorageService } from 'ngx-webstorage';

@Component({
  selector: 'app-timer-task-creation-dialog',
  templateUrl: './timer-task-creation-dialog.component.html',
  styleUrls: ['./timer-task-creation-dialog.component.styl']
})
export class TimerTaskCreationDialogComponent {

  public tpl:TaskCreationPayload = new TaskCreationPayload();

  constructor(public dialogRef: MatDialogRef<TimerTaskCreationDialogComponent>,
              private sessionStorage: SessionStorageService,
              @Inject(MAT_DIALOG_DATA) public resumedialog: TaskCreationPayload) {
    if (resumedialog != null) {
      this.tpl = resumedialog;
    } else {
      this.tpl.type = "TIMER";
    }

    dialogRef.keydownEvents().subscribe(e => this.triggerStateSave());
    dialogRef.backdropClick().subscribe(e => this.onNoClick());
  }

  onNoClick(): void {
    this.sessionStorage.clear('resumetask');
    this.dialogRef.close();
  }

  triggerStateSave() {
    this.sessionStorage.store('resumetask', this.tpl);
  }
}
