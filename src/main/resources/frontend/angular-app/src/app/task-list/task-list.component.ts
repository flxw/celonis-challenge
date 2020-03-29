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

  downloadResult(taskId:string) {
    /* TODO trigger download here
    * > add type field for filtering in UI
    * > refactor task creation endpoints to be separate for projectgeneration and timer
    * > task creation wizard
    */
  }

  filterOnTaskType(type:string) {
    return this.taskList.filter(task => task.type = 1);
  }
}
