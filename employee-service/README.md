# Employee-service
The spring boot java microservice allows you run create departments and then add, update and delete employee to it.
It exposes a Rest Api for interaction, check Swagger for more details of Api and Models.

```
Swagger: 
        localhost:8080
``` 

### Topics
  * [Assumptions](#assumptions)
  * [What does it do ?](#technical-details)
  * [How to use it ?](#guide)
  * [Tech Stack](#tech-stack)
  * [Screen-shot](#screen-shot)
  
###  [Assumptions](#assumptions)  
1. This service only published employee UUID, Type(Create, Update or Delete) and timestamp.
2. First there are multiple validation i have added to allow valid data for department and employee attributes.
3. Email of an employee cannot be updated as it is unique.
4. Basic authentication is enabled for Create, Update & Delete (user: admin, password: admin).
5. If you try to create or update department of an employee which doesnt exist , error is returned.
6. This service publishe an event to RabbitMQ queue for each CREATE, UPDATE and DELETE.
7. There is basic authentication for Employee Controller which take in account the login and role for operations.
  
###  [What does it do ?](#technical-details)
This api allows one to create departments. And then allows one to Create, Update and Delete employees mapped to department.
For every Create, Update and Delete, an event is published to another microservice called [Event-Service](https://github.com/anusheelchandra/employee-event-service/tree/master/event-service)

###  [How to use it ?](#guide)

Local Run with test profile from employee-service project
```
   1. mvn clean install -DskipTests=True
   
   2. Make sure rabbitMQ is running or else run : 
    a. docker pull rabbitmq:3-management    
    b. docker run -d --hostname my-rabbit --name my-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
   
   2. mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=test" 
    
```

Docker
```  
Please refer below link to the parent project README file for running it inside Docker.
```
[Employee-Event-Service](https://github.com/anusheelchandra/employee-event-service)

### [Tech Stack](#tech-stack)
Java 11, Swagger, Spring Boot, Spring Cloud Stream, Spring Amqp, RabbitMQ, Maven, Lombok, Mockito, Junit5, AssertJ, 
Spring Data JPA and MySQL and H2 database(testing) for persistence and docker and docker-compose.
This microservice has been built with IntelliJ IDE and formatted with google-java-format. 


### [Screen-shot](#screen-shot)
![alt text](https://github.com/anusheelchandra/employee-event-service/blob/master/employee-service/src/test/resources/ScreenShot1.png)
![alt text](https://github.com/anusheelchandra/employee-event-service/blob/master/employee-service/src/test/resources/ScreenShot2.png)
![alt text](https://github.com/anusheelchandra/employee-event-service/blob/master/employee-service/src/test/resources/ScreenShot3.png)  
