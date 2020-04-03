import { Injectable, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TaskCreationPayload } from './task-creation-payload';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
    'Celonis-Auth': 'totally_secret'
  })
};

@Injectable({
  providedIn: 'root'
})
export class TaskService implements OnInit {
  // TODO: port forward the service here
  private SERVER_ADDRESS:string = "http://localhost:30163";
  private TASK_API:string ="/api/tasks/";

  constructor(private http: HttpClient) { }

  ngOnInit() {
  }

  getTasks() {
    let taskUrl = this.SERVER_ADDRESS + this.TASK_API;
    return this.http.get(taskUrl, httpOptions)
  }

  cancelTask(taskId) {
    let requestUrl = this.SERVER_ADDRESS + this.TASK_API + taskId + "/cancel";
    return this.http.post(requestUrl, null, httpOptions);
  }

  deleteTask(taskId) {
    let requestUrl = this.SERVER_ADDRESS + this.TASK_API + taskId;
    return this.http.delete(requestUrl, httpOptions);
  }

  startTask(taskId) {
    let requestUrl = this.SERVER_ADDRESS + this.TASK_API + taskId + "/execute";
    return this.http.post(requestUrl, null, httpOptions);
  }

  createTask(tpl:TaskCreationPayload) {
    let requestUrl = this.SERVER_ADDRESS + this.TASK_API;
    return this.http.post(requestUrl, tpl, httpOptions);
  }
}
