# DB Tester

We have many database queries in ADBS and RTDS written by our user that do not have automated tests.
If our users where to be able to define test cases for their queries they could change them more safely.

Ubiquitous Language:
- given == data to prepare a test
- query == the database query to test
- want == the expected result
- got == the result of the query after execution on the db
- case == a test case including given, query and want
- suite == a test suit consisting of multiple cases

Executing a test case:
- insert given data into database
- execute query
- compare the result you got with the thing you want

Initial focus on MongoDB
- here given, query, want and got are all in json format.

# TODO

### Rest API to read and write given, query, and want
- the user wants to create, read, update and delete (CRUD) givens, queries and wants
- use @RequestBody document: Document to get the body from the http request
- http methods: GET = read, POST = create, PUT = update, DELETE = remove
- you do not need to use POST; a single PUT can serve as both (upsert)
- automated tests with spring (send http request)

To call your rest api you can use Postman or curl.

#### Comment by Manuel
- when Put is called checking for Entry in DB => if Item not existing Feedback => to use PostMethod instead (since far Console Output)

### Ideas
- spring config
- persist given, query and want in a mongodb
- if got != want: what is the difference? show both? show where they differ? -D
- have a rest endpoint to execute a test suite -D
- build a web UI to show how test cases might be presented -iP
- postgres test cases
- OpenAPI description of the API -D

#### -D => Done // -iP => in Process

### Persistence
The test cases have to be stored.
Since we use MongoDB in ADBS/RTDS we should use it here as well.
This means every http request creates a database statement.

## Config
If you deploy an application in multiple environments some it usually has to be configured.
If the app uses a database the connection string might be different for each environment.
For this you can read the configuration from a file or from environment variables.

Environment variables are part of the operating system.
Each docker container also has its own environment variables. 
You can see them in Unix-like systems using "env" in the terminal.
Spring has a built-in way how to do configuration.

## Feedback when test case fails
If a test fails (got != want) the user needs to know why it failed.
A start is to tell the user how got differs from want.
Also, if any errors occur the user should be notified. 

## Rest endpoint to execute a test case or suite
If the user want to run some tests he should be able to do so via rest api. 

## Web UI
build a web UI to show how test cases might be presented to our BC colleagues.
If this is done well we might be able to integrate it into the actual application.
(Angular?)

## OpenAPI description of the API
The current way to describe a rest api is via open api specification.
This specification comes in the form of a yaml file.
This yaml file can be served by a webapp.
You can test the http requests in this app.
(In spring openapi is done via annotations)

# Comments by Alex

## 25.08.2022

### Prefer val over var in Kotlin
Val is like const in Typescript. My Intellij marks all unnecessary vars yellow.

### Junit
Included Junit Example.
As you have already seen you need an entry point for local development to get code execution going.
I usually use test cases if I develop something like a REST API.

### Example Testcase
Included Example Files for a testcase: given data, query, wanted result
The goal is to insert the given data in the mongodb, execute the query and check if the result is as expected.
You can try the testcase in compass.

### Included MongoTemplate Example
This is the variant of Spring MongoDB access we use everywhere our project.

## 24.08.2022

### Spring Application Context
The Spring Application Context is contains beans.
Beans are instances of Java Classes.
For example, Spring finds classes annotated with @Service on startup.
This is called ComponentScan and is enabled by the @ComponentScan annotation (included in @SpringBootApplication).
In this case every service is instantiated once (Singleton) given that Spring can find the required
constructor parameters in the application context.
There are other beans that do configuration.
For example, they come with spring boot starters which can be included as a maven dependency (we have some in this project).

### gitignore
If you use git put a .gitignore file next to he .git folder.
Files starting with a dot are hidden in Unix (macOS is Unix-like).
You have to use "ls -a" to see them in the terminal and press "command + shift + ." in Finder.
With this file you can exclude files from git.
This is used for intellij specific files and output from maven (or compilation in general).
In Angular the .gitignore File is created when you scaffold your project with the cli (ng new) and excludes the node_modules folder.

### start database in docker container
Its best not to install database or messaging directly on your machine.
This way you can get rid of the database easily and keep your machine cleaner.
Start them in a container using docker-compose.

- docker-compose -f mongo-docker-compose.yaml up -d
- docker-compose -f mongo-docker-compose.yaml down
