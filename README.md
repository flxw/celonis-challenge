## Backend module

### Challenge 1: API Design

> This challenge is about designing the API for MarsGate. MarsGate is an Application Management Tool which allows humans to apply for the "Free ticket to planet Mars" lottery.
> 
> In MarsGate, users are able to:
> - List his/her applications (including unfinished ones)
> - Create a new application
> - The application process has those steps
>     - Step 1: Add a CV
>     - Step 2: Add personal details
>     - Step 3: Write an essay
>     - Step 4: Summary showing data from the previous steps in a read-only form
> - Submit an application
> - Cancel/delete an application
> 
> The user will be able to start multiple applications at the same time and is not required to finish the whole application at once. So, he can start by filling a few steps and come back later to complete and submit the application, or cancel/delete it. In the list of applications that he/she created, the user will see the progress of each application.
The OpenAPI 2.0 specification for the MarsGate API proposal can be found in the the `docs/marsgate-api.yaml`file.

### Challenge 2: Completing and extending the java application

#### Task 1: Use the provided API to download the base project
> We provided the API documentation as a swagger yaml specification that you can open in https://editor.swagger.io/. The application that implements this documentation is currently running on heroku at https://pure-plains-73336.herokuapp.com/. You should use the app (e.g. via swagger editor) to create a task, execute it and get the result. The result is a zip file that contains the base project for the upcoming tasks. The header authentication secret (named Celonis-Auth) is "totally_secret".

Used the Swagger documentation to create a new task, trigger it and download its result.
While using it I thought that it could be optimized, since it does not make sense to me to provide an ID or creation date and get another one as return. I did that when implementing the tasks below.

#### Task 2: Dependency injection
> The project that you downloaded in Task 1 fails to start correctly due to a problem in the dependency injection.
> Identify that problem and fix it.

If I recall correctly, this was a circular dependency.
I refactored both classes together since I felt they were revolving around the same thing - tasks.

#### Task 3: Packaging
> The packaging of the project is incorrect. The expected result of the maven packaging phase
> would be a standalone runnable jar containing every dependency (runnable via java -jar challenge.jar).
> Your frontend application should also be packed into the jar.

Done.

#### Task 4: Extending the application
> The task in this challenge is to extend the current functionality of the backend by
> - implementing a new task type and
> - show the progress of the task execution
> - implement task cancellation mechanism.
>
> The new task type is a simple counter which is configured with two input parameters, `x` and `y` of type `integer`. When the task is executed, counter should start in the background and progress should be monitored. Counting should start from `x` and get increased by one every second. When counting reaches `y`, the task should finish successfully.
>
> The progress of the task should be exposed via the API so that the web client can monitor it. Canceling a task that is being executed should be possible, in which case the execution should stop.

This task led me to do several things:
1. Create an abstract base class _Task_, and derive a class to generate projects and a class for timer tasks from it
2. The parent class Task provides the attributes that both tasks share and abstract methods, e.g. for scheduling. This allows for task execution via a common interface for both kinds of tasks
4. Adapt the JpaRepository to handle the bean hierarchy, as well as configure Spring to put the beans into a single table
4. Add a state field to the base class to track different task states such as ready, in execution, and done
4. Task progress is persisted via a listener on scheduler job executions
4. Add a class that carries task creation data, since one endpoint should be used to both kinds of tasks, and it should not be necessary to pass IDs for task creation
5. Update the documentation inside `docs/task-api.yaml`

#### Task 5: Periodically cleaning up the tasks
> The API can be used to create tasks but the user is not required to execute those tasks. The tasks that are not executed after an extended period of time (e.g. a week) should be periodically cleaned up (deleted).

Via a scheduled job, triggered every 7 days, all tasks older than 7 days are deleted.
This task is also scheduled by the TaskService.

## Frontend module
### Challenge 1: Web client
> This challenge is about building the web client for the resulting application from challenge 2.
> The requirements of this web client are demonstrated with the Figma mockup - [Link to mockup](https://www.figma.com/proto/QU16smviKOMZek8twvbxw1ap/Full-stack---challenge-02).
> 
> The application consists of a dashboard which lists all the tasks (including the tasks that are never executed). From the list of tasks, the user can:
> - create new tasks
> - open an existing task
> - cancel tasks that are being executed and
> - delete tasks
> 
> For creating new tasks, a wizard should be implemented which has multiple steps. The steps are:
> 1. Choose the type of the scheduled task
> 2. Configure the chosen scheduled task
> 3. Execute and monitor progress
> 4. Summary with results
> 
> The user might leave the wizard at any step but he/she should still be able to continue later and finish it. The user should be able to refresh the browser tab and continue from the step where he left.
> 
> What we are looking into:
> - Technical understanding of a provided API
> - Ability to execute / implement
> - Frontend project setup and API usage
> - Frontend implementation skills (Javascript, Typescript, HTML/CSS)
> - Frontend component structure and routing

In the application, tasks are separated by type and can be executed, canceled or deleted.
Results can also be downloaded, if applicable.
The UI does not match the proposed one by far, but I believe that it delivers on the requirements.
I took the liberty to reduce the task creation process to the bare minimum to make it faster and easier for the user.
In the specification, it was 5 clicks.
In my proposal, there are no IDs anymore, just name, task-specific extra data and the task is created in 3 clicks.
There is a progress bar on the UI that shows how far a task is, and it can be cancelled or deleted, too.
To save the user from having to press "refresh" all the time, I added an auto-update button.

## Cloud Native Module
### Challenge 1: Running on Kubernetes
To run the challenge, you need to have PostgreSQL running on your machine on the default port
and have a database ``challenge`` running on it. The user `challenge` has access to it,
and his password is `password123`. 

#### Task 1: Dockerize the application
> To run the application on Kubernetes, it needs to be containerized.
> Adapt the project so that it produces a Docker image of the application.

I created a Dockerfile. Simply run `docker build -t <IMAGE_TAG> .`  to produce a docker image for the application.
By itself it won't run though, as it requires certain environment variables. These are defined in the Kubernetes manifest

#### Task 2: Write Kubernetes manifests
> Prepare manifests to run the application on Kubernetes.
> 
> In the end the deployment should fulfill the following requirements:
> - Application updates should be possible with zero downtime
> - Data is persisted across application restarts
> - Configuration, e.g. database credentials or the header authentication secret are not baked into the container

Do `kubectl apply -f kubernetes/deployment.yaml` to set up the whole deployment. It does the following:

1. Create a deployment with 3 pods and a rolling release strategy, allowing zero-downtime deployments
2. Access to the local PostgreSQL DB via the service `postgres-db-svc`
3. Database credentials and the auth header are injected to the pods as environment variables from a Kubernetes secret
5. The whole deployment is exposed to `localhost:30163` via a service that routes traffic to one of the 3 pods

This task also transformed the way I though about applications like therse.
Due to the fact that it runs in ephemeral pods, it helps a little to think like a DBMS in ACID terms.
The application needs to process data in a transactional fashion, and only persist valid states.
This allows for different pods to take over if the other pod fails - without leaving the user's data in an undefined state.
Initially I used shedlock to prevent schedules from executing at the same time, but this requirement had me switch to Quartz.

#### Task 3: What's missing?
I think some sort of user management is missing.
Furthermore there should be a limit to how many tasks a session or IP is allowed to created and/or run at the same time.
I have not worked a lot with scheduling before so I am not sure how much load one trigger and job per executing task causes, and if it would not be better to bundle it (like in my first implementation with Shedlock).

UI-wise I would would like to get rid of requests for progress checking altogether and use websockets so that the UI can auto-update.
Also, I would like to add a download button to download that task data from the UI.

On the deployment, I would prefer to serve the UI not from the pods, but from a separate array of nginx pods.
I believe that this would achieve better performance and additionally a seperation of frontend and backend on the pod level.
