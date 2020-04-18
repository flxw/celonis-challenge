import { Component, Inject } from '@angular/core';
import { TaskCreationPayload } from '../task-creation-payload';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SessionStorageService } from 'ngx-webstorage';

@Component({
  selector: 'app-projectgeneration-task-creation-dialog',
  templateUrl: './projectgeneration-task-creation-dialog.component.html',
  styleUrls: ['./projectgeneration-task-creation-dialog.component.styl']
})
export class ProjectgenerationTaskCreationDialogComponent {
  public tpl:TaskCreationPayload = new TaskCreationPayload();

  constructor(public dialogRef: MatDialogRef<ProjectgenerationTaskCreationDialogComponent>,
              private sessionStorage: SessionStorageService,
              @Inject(MAT_DIALOG_DATA) public resumedialog: TaskCreationPayload) {
    if (resumedialog != null) {
      this.tpl = resumedialog;
    } else {
      this.tpl.type = "PROJECTGENERATION";
    }

    dialogRef.keydownEvents().subscribe(e => this.triggerStateSave());
    dialogRef.afterClosed().subscribe(e => this.onNoClick());
  }

  onNoClick(): void {
    this.sessionStorage.clear('resumetask');
    this.dialogRef.close();
  }

  triggerStateSave() {
    this.sessionStorage.store('resumetask', this.tpl);
  }
}
