# Spring, Microservices, Kafka

Backend framework notes plus a small Spring Boot project.

## Notes

- [Spring Boot Q&A](notes/Spring_Boot_QA.md)
- [Spring Security Q&A](notes/Spring_Security_QA.md)
- [Spring transaction management](notes/Spring_Transaction_Management.md)
- [Hibernate and JPA Q&A](notes/Hibernate_JPA_QA.md)
- [Microservices Q&A](notes/Microservices_QA.md)
- [Kafka Q&A](notes/Kafka_QA.md)

## orders-springboot-project

Minimal Spring Boot REST service (controller, DTO, global exception handler, tests). Open with Maven.

- [application.properties](orders-springboot-project/application.properties)
- [ErrorResponse.java](orders-springboot-project/ErrorResponse.java)
- [GlobalExceptionHandler.java](orders-springboot-project/GlobalExceptionHandler.java)
- [OrderController.java](orders-springboot-project/OrderController.java)
- [OrderControllerTest.java](orders-springboot-project/OrderControllerTest.java)
- [OrderDTO.java](orders-springboot-project/OrderDTO.java)
- [OrderNotFoundException.java](orders-springboot-project/OrderNotFoundException.java)
- [OrdersApplication.java](orders-springboot-project/OrdersApplication.java)
- [OrdersApplicationTests.java](orders-springboot-project/OrdersApplicationTests.java)
- [pom.xml](orders-springboot-project/pom.xml)

## snippets

Sample `application.properties` for an H2 + JPA setup.

- [application-h2-sample.properties](snippets/application-h2-sample.properties)

