import { Component } from '@angular/core';
import { TaskService } from './task.service';
import { MatDialog } from '@angular/material/dialog';
import { TimerTaskCreationDialogComponent } from './timer-task-creation-dialog/timer-task-creation-dialog.component';
import { ProjectgenerationTaskCreationDialogComponent } from './projectgeneration-task-creation-dialog/projectgeneration-task-creation-dialog.component';
import { TaskCreationPayload } from './task-creation-payload';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent {
  title = 'angular-app';

  constructor(private taskService:TaskService,
              private dialog: MatDialog) {
  }

  updateTaskList():void {
    this.taskService.refreshTasks();
  }

  openTimerTaskCreationDialog(): void {
    const dialogRef = this.dialog.open(TimerTaskCreationDialogComponent, {
      width: '600px',
    });

    let that = this;
    dialogRef.afterClosed().subscribe(r => that.createTaskFromPayload(r, that));
  }

  openProjectgenerationTaskCreationDialog(): void {
    const dialogRef = this.dialog.open(ProjectgenerationTaskCreationDialogComponent, {
      width: '600px',
    });

    let that = this;
    dialogRef.afterClosed().subscribe(r => that.createTaskFromPayload(r, that));
  }

  createTaskFromPayload(payload:TaskCreationPayload, that:any) {
    if (payload == undefined) return;
    that.taskService.createTask(payload).subscribe(r => this.updateTaskList());
  }
}
