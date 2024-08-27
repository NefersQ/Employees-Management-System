# Employees-Management-System
It is a project that manages employee database.
Main technologies are: Java 17,openJDK 16, Spring(Boot,Web,Jpa),Oracle Database.

Package structure includes business, model and web.controller packages. Business is responsible for containing repository,DAO models, mappers and services.
Model package contains only DTO. Web.controller package contains only controllers of the application.

To execute this project you should open the repository in Intellij and choose corresponding Java and/or JDK versions if needed.
Then in application.properties you should choose your version and URL of Oracle Database and enter username and password.
After successful program execution you can open Postman collection added to this repository to use endpoints.

Main endpoints are:
GET: Get all employees. Get a list of all employees stored in database.
GET: Export all employees by filter via pushing department and date parameters. 
POST: Create and employee by entering name,department,joindate.
DELETE: To delete an employee by entering ID.
