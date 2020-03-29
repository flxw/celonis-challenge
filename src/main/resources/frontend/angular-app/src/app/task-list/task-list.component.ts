import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.styl']
})
export class TaskListComponent implements OnInit {
  public taskList:any;

  constructor(private taskService:TaskService) { }

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

  filterOnTaskType(type:string) {
    return this.taskList.filter(task => task.type == type);
  }
}
