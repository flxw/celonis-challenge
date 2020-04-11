import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.styl']
})
export class TaskListComponent implements OnInit {
  public taskList:any = [];

  constructor(public taskService:TaskService) { }

  ngOnInit(): void {
    this.taskService.refreshTasks();
  }

  cancelTask(taskId:string):void {
    this.taskService.cancelTask(taskId).subscribe(r => this.taskService.refreshTasks());
  }

  deleteTask(taskId:string) {
    this.taskService.deleteTask(taskId).subscribe(r => this.taskService.refreshTasks());
  }

  startTask(taskId:string) {
    this.taskService.startTask(taskId).subscribe(r => this.taskService.refreshTasks());
  }
}
