# Event-service
The spring boot java microservice allows you run read all the event publishe for actions taken on employee i.e. CREATE, UPDATE and DELETE.
This service listens to all the event published by [Employee-Service](https://github.com/anusheelchandra/employee-event-service/tree/master/employee-service) using RabbitMQ exchange.
It exposes a Rest Api for interaction, check Swagger for more details of Api and Models.

```
Swagger: 
        localhost:8081
``` 

### Topics
  * [Assumptions](#assumptions)
  * [What does it do ?](#technical-details)
  * [How to use it ?](#guide)
  * [Tech Stack](#tech-stack)
  * [Screen-shot](#screen-shot)
  
###  [Assumptions](#assumptions)  
1. First there is validation for uuid being pass to controller.
2. Event published by employee-service are listened and persisted in this service's DB.
4. This service listens events from RabbitMQ queue for each CREATE, UPDATE and DELETE done by employee-service.
  
###  [What does it do ?](#technical-details)
This api allows one to fetch all event for a particular employee using Uuid.
Events are listened and persisted for Create, Update and Delete done on employees.

###  [How to use it ?](#guide)

Local Run with test profile from event-service project :
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
Java 11, Swagger Spring Boot, Spring Cloud Stream, Spring Amqp, RabbitMQ, Maven, Lombok, Mockito, Junit5, AssertJ, 
Spring Data JPA and MySQL and H2 database(testing) for persistence and docker and docker-compose.
This microservice has been built with IntelliJ IDE and formatted with google-java-format.


### [Screen-shot](#screen-shot)
![alt text](https://github.com/anusheelchandra/employee-event-service/blob/master/event-service/src/test/resources/ScreenShot.png)