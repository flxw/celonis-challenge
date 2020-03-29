import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import { TaskListComponent } from './task-list/task-list.component';
import {MatListModule} from '@angular/material/list';
import { HttpClientModule }    from '@angular/common/http';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';
import {MatStepperModule} from '@angular/material/stepper';
import {MatRadioModule} from '@angular/material/radio';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import { TimerTaskCreationDialogComponent } from './timer-task-creation-dialog/timer-task-creation-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    TaskListComponent,
    TimerTaskCreationDialogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatListModule,
    HttpClientModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatStepperModule,
    MatRadioModule,
    FormsModule,
    MatInputModule,
    MatMenuModule
  ],
  entryComponents: [TimerTaskCreationDialogComponent],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
