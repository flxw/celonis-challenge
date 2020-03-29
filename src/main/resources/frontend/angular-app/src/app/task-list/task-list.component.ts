import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { TimerTaskCreationDialogComponent } from '../timer-task-creation-dialog/timer-task-creation-dialog.component';

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
    * > task creation wizard (what else needs to be set except name?! nothing right....)
    * > Refactor Task Runner so it does not need to know about timer logic
    * > Clean up task formatting with enum location etc
    * > finalize challenge today!
    */
  }


  openTimerTaskCreationDialog(): void {
    const dialogRef = this.dialog.open(TimerTaskCreationDialogComponent, {
      width: '600px',
    });

    let that = this;

    dialogRef.afterClosed().subscribe(payload => {
      that.taskService.createTask(payload).subscribe(r => this.updateTaskList());
    });
  }

  openProjectgenerationTaskCreationDialog(): void {
    /*const dialogRef = this.dialog.open(TaskCreationDialogComponent, {
      width: '600px',
      height: '300px'
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });*/
  }

  filterOnTaskType(type:string) {
    return this.taskList.filter(task => task.type == type);
  }
}
