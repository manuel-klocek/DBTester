# DB Tester

We have many database queries in ADBS and RTDS written by our user that do not have automated tests.
If our users where to be able to define test cases for their queries they could change them more safely.

A test case has three steps:
1. Prepare a database with test data
2. Execute a database query
3. Check the results

Initially we focus on MongoDB.
Here, input data, query and output data are all in json format.

To start we consider the following scenario:
1. fill a single collection with data
2. run an agrgegation on it
3. check if the aggregatione returns the correct number of documents

# Comments by Alex

## Possible TODOs
- spring config

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
