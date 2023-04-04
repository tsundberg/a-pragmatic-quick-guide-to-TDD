# The implementation, step by step

## The problem

The goal is to show a pragmatic use of Test-Driven Development from a seemingly real life experience.

This presents a few problem. We don't know the same domain. I have only some 30 minutes to show a way of working that
works extremely well for me.

I prefer, when possible, to do outside in development. This ensures that whatever I build is aimed for usage and not
hidden somewhere in the middle of a big ball of mud.

## An endpoint for creating and getting tasks todo

Create a `TodoController` with two methods

* Create a task
* Get a list of tasks

## A service that that will connect the controller to the rest of the system

Create a TodoService that receives a domain object for creating a task and return a list of domain objects with task for
a person.

## A repository that receives domain objects

Create a repository that will be able to hold tasks for users

## And endpoint

We have something that seems to work. But it should be an endpoint. And it isn't.

In order for that to happen, we need to wire it up as an endpoint. And make sure the controller receives the
dependencies it needs.

But we test drive stuff, so we will create an integration test that will connect to a running instance, connect to an
endpoint for adding a task and an endpoint for getting the tasks.

The endpoint works and can be tested by our consumers

However, it is rather useless as it will not remember anything after a program restart.

## Add proper persistence support

A large step with lots of moving parts

Create 

* A Datasource - don't call it Datasource because that will disturb Spring. Call it DatasourceConfiguration
* Add an application-dev.properties with a JDBC URL with `tc` in the url. This will trigger Test containers to fire upa PostgreSQL
* Add a flyway location property so Flyway knows where to find the migration files in application.properties
* Separate the test so we can run the fast in memory as well as the slower integration test
* Have the SQL Repository implementation to use an interface to JDBI and thus be able to talk to the database
* Implement a TodoDao
* Add a TodoMapper to transform a SQL respons to a domain object
* Add the database table needed in `V001__create_todo_table.sql`

In order to start testcontainers from a test, I had to create a link to `~/.docker/run/docker.sock`
from `/var/run/docker.sock` using the command

```
sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
```

