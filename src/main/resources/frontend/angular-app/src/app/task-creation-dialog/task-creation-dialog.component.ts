import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-task-creation-dialog',
  templateUrl: './task-creation-dialog.component.html',
  styleUrls: ['./task-creation-dialog.component.styl']
})
export class TaskCreationDialogComponent {

  constructor(public dialogRef: MatDialogRef<TaskCreationDialogComponent>) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}

/*
@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'dialog-overview-example-dialog.html',
})
export class DialogOverviewExampleDialog {

  constructor(
    public dialogRef: MatDialogRef<DialogOverviewExampleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}*/