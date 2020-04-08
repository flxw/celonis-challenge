import { Component, OnInit } from '@angular/core';
import { TaskCreationPayload } from '../task-creation-payload';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-projectgeneration-task-creation-dialog',
  templateUrl: './projectgeneration-task-creation-dialog.component.html',
  styleUrls: ['./projectgeneration-task-creation-dialog.component.styl']
})
export class ProjectgenerationTaskCreationDialogComponent {
  public tpl:TaskCreationPayload = new TaskCreationPayload();

  constructor(public dialogRef: MatDialogRef<ProjectgenerationTaskCreationDialogComponent>) {
    this.tpl.type = "projectgeneration";
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
