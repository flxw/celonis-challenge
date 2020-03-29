import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { TaskCreationDialogComponent } from '../task-creation-dialog/task-creation-dialog.component';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.styl']
})
export class TaskListComponent implements OnInit {
  public taskList:any = [];

  constructor(private taskService:TaskService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.updateTaskList();
  }

  updateTaskList():void {
    this.taskService.getTasks().subscribe(r => this.taskList = r);
  }

  cancelTask(taskId:string):void {
    this.taskService.cancelTask(taskId).subscribe(r => this.updateTaskList());
  }

  deleteTask(taskId:string) {
    this.taskService.deleteTask(taskId).subscribe(r => this.updateTaskList());
  }

  startTask(taskId:string) {
    this.taskService.startTask(taskId).subscribe(r => this.updateTaskList());
  }

  downloadResult(taskId:string) {
    /* TODO trigger download here
    * > task creation wizard
    * > Refactor Task Runner so it does not need to know about timer logic
    * > Clean up task formatting with enum location etc
    * > finalize challenge today!
    */
  }


  openTaskCreationDialog(): void {
    const dialogRef = this.dialog.open(TaskCreationDialogComponent, {
      width: '250px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  filterOnTaskType(type:string) {
    return this.taskList.filter(task => task.type == type);
  }
}
