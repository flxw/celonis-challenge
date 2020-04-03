## Backend module

### Challenge 1: API Design

This challenge is about designing the API for MarsGate. MarsGate is an Application Management Tool which allows humans to apply for the "Free ticket to planet Mars" lottery.

In MarsGate, users are able to:
- List his/her applications (including unfinished ones)
- Create a new application
- The application process has those steps
    - Step 1: Add a CV
    - Step 2: Add personal details
    - Step 3: Write an essay
    - Step 4: Summary showing data from the previous steps in a read-only form
- Submit an application
- Cancel/delete an application

The user will be able to start multiple applications at the same time and is not required to finish the whole application at once. So, he can start by filling a few steps and come back later to complete and submit the application, or cancel/delete it. In the list of applications that he/she created, the user will see the progress of each application.

The deliverable for this challenge is the documentation of the API which can be used by another team to build the web client. For designing and documenting the API, https://swagger.io/ can be used.

What we are looking into:
- Problem understanding skills
- API design principles
- Knowledge regarding HTTP protocol

Note: There is no implementation needed for this challenge

### Challenge 2: Completing and extending the java application

#### Task 1: Use the provided API to download the base project
Used the Swagger documentation to create a new task, trigger it and download its result.

#### Task 2: Dependency injection
If I recall correctly, this was a circular dependency.
I refactored both classes together since I felt they were revolving around the same thing - tasks.

#### Task 3: Packaging
Done

#### Task 4: Extending the application
This task led me to do several things:
1. Create an abstract base class _Task_, and derive a task to generate projects and a timer task from it
2. The parent class Task provides the attributes that both tasks share
3. With the given superclass Task, it was easy to leverage polymorphism to execute a task irrespective of its type
4. The JpaRepository was also adapted to use the superclass
4. Via a state field, executing or canceled states can be recorded
4. The state of task execution is not where I would like it yet, currently it is a method that increments
the executed timer tasks every 1s, and it runs irrespective of any available tasks.
I would like to make that depend on the specific task, but was not able how to make that respect the short-livedness of containers
4. Task progress is also offered via a method that the Task subclasses need to implement

#### Task 5: Periodically cleaning up the tasks
1. Via a scheduled task, all tasks older than 7 days are deleted

## Frontend module
### Challenge 1: Web client

In the application, tasks are separated by type and can be executed, canceled or deleted.
Results can also be downloaded, where applicable.

The user might leave the wizard at any step but he/she should still be able to continue later and finish it. The user should be able to refresh the browser tab and continue from the step where he left.

I took the liberty to reduce the task creation process to the bare minimum
to make it faster and easier for the user. No IDs anymore,
just name, relevant extra data and the task is created in <5 clicks.

## Cloud Native Module
### Challenge 1: Running on Kubernetes
To run the challenge, you need to have PostgreSQL running on your machine on the default port
and have a database ``challenge`` running on it. The user `challenge` has access to it,
and his password is `password123`. 

#### Task 1: Dockerize the application
Run `docker build -t <IMAGE_TAG> .`  to produce a docker image for the application.
By itself it won't run though, as it requires certain environment variables. 

#### Task 2: Write Kubernetes manifests
Do `kubectl apply -f kubernetes/deployment.yaml` to set up the whole deployment.

It does the following:
1. Create a deployment with 3 pods and a rolling release strategy, allowing zero-downtime deployments
2. Access to the local DB is granted via a `postgres-db-svc` service
3. Database credentials and the auth header are injected to the pods as environments from a Kubernetes secret
4. The application process data in a transactional fashion, so the state is persisted
5. The whole deployment is exposed to `localhost:30163` via a service that routes traffic to one of the 3 pods

#### Task 3: What's missing?
I would clean up the implentation of the schedules, so that it incurs less unneccessary load.
Furthermore, I think there should be a limit to how many tasks a user is allowed to create
and/or run at the same time. UI-wise I would replace the refresh button with a timed refresh,
or even better get rid of requests for progress checking altogether and use websockets.
Finally, I would serve the UI not from the pods where it is auth-header protected,
but from an array of nginx pods.
This would achieve additionally a seperation of frontend and backend on the pod level.