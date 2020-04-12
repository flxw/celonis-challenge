import { Component, Inject } from '@angular/core';
import { TaskService } from './task.service';
import { MatDialog } from '@angular/material/dialog';
import { TimerTaskCreationDialogComponent } from './timer-task-creation-dialog/timer-task-creation-dialog.component';
import { ProjectgenerationTaskCreationDialogComponent } from './projectgeneration-task-creation-dialog/projectgeneration-task-creation-dialog.component';
import { TaskCreationPayload } from './task-creation-payload';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { SessionStorageService } from 'ngx-webstorage';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent {
  public title:string = 'angular-app';
  public autorefreshEnabled:boolean = false;
  public canResumeTaskCreation:boolean = false;
  private intervalId:any;

  constructor(private sessionStorage: SessionStorageService,
              private taskService:TaskService,
              private dialog: MatDialog) {
    // recover task creation boolean from session storage
    // and set default value if not present
    this.canResumeTaskCreation = this.sessionStorage.retrieve('resumetask') != null;
    this.sessionStorage.observe('resumetask').subscribe(b =>  this.canResumeTaskCreation = (b != null));

    if (this.canResumeTaskCreation == null) {
      this.canResumeTaskCreation = false;
    }
  }

  onSlideToggleChange(event:MatSlideToggleChange):void {
    this.autorefreshEnabled = event.checked;

    if (this.autorefreshEnabled) {
      this.intervalId = setInterval(() => this.updateTaskList(), 1000);
    } else {
      clearInterval(this.intervalId);
    }
  }

  updateTaskList():void {
    this.taskService.refreshTasks();
  }

  openTimerTaskCreationDialog(): void {
    let resumeData = this.sessionStorage.retrieve('resumetask');
    const dialogRef = this.dialog.open(TimerTaskCreationDialogComponent, {
      width: '600px',
      data: resumeData
    });

    let that = this;
    dialogRef.afterClosed().subscribe(r => that.handleDialogCompletion(r, that));
  }

  openProjectgenerationTaskCreationDialog(): void {
    let resumeData = this.sessionStorage.retrieve('resumetask');
    const dialogRef = this.dialog.open(ProjectgenerationTaskCreationDialogComponent, {
      width: '600px',
      data: resumeData
    });

    let that = this;
    dialogRef.afterClosed().subscribe(r => that.handleDialogCompletion(r, that));
  }

  resumeTaskCreationDialog():void {
    let cancelledTaskData = this.sessionStorage.retrieve('resumetask');

    if (cancelledTaskData.type == 'TIMER') {
      this.openTimerTaskCreationDialog();
    } else if (cancelledTaskData.type == 'PROJECTGENERATION') {
      this.openProjectgenerationTaskCreationDialog();
    }
  }

  handleDialogCompletion(payload:TaskCreationPayload, that:any) {
    if (payload == undefined) return;

    that.taskService.createTask(payload).subscribe(r => this.updateTaskList());
  }
}
