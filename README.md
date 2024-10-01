# human-resource-manager-service
 A sample human resource management service developed in Spring Boot - Kotlin. Uses Gradle, Spring Security, a custom Role Based Access Control System (RBAC), PostgreSQL DB, a Simple JWT Token-based authentication and authorization with a simple username password login system

Technologies Used:
* Kotlin
* Gradle
* Spring Boot
* Spring Security
* RBAC
* Simple JWT Token Based Authentication and Authorization
* PostgreSQL DB

# Setup
* Install PostgreSQL; you can install it from official website https://www.postgresql.org/download/ OR install via Docker
* Go to /src/main/resources/application.yml
* Replace the <i><b>port</b></i> in  <b>jdbc:postgresql://localhost:<i><b><<port>port></b></i>/<database_name></b>  with the port of your PostgreSQL and <b><i><database_name></i></b> with the database name
* Run the application using <b>HumanResourceManagerServiceApplication.kt</b>
