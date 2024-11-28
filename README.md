Overview
This application allows users to create and retrieve payment records. It includes features like custom validation for Baltic country IBANs, resolving the client's country based on their IP address, and uploading payments via CSV files.

Features
Create Payment: Submit payment details with validation.
Upload Payments: Bulk upload payments using a CSV file.
Retrieve Payments: Fetch all payments or filter by debtor IBAN.
Validation: Custom annotation to validate Baltic IBANs.
Country Resolution: Determines client's country from IP address.
Architecture
The application follows a standard Spring Boot architecture with layered components:

Controller: Handles HTTP requests and responses.
Service: Contains business logic.
Repository: Interacts with the database.
Model: Represents the data structure.
Validation: Custom annotations and exception handling.
Technologies Used
Java 21
Spring Boot
Spring Data JPA
Hibernate Validator
Jakarta Validation
Apache Commons CSV
Lombok
SLF4J (Logging)
Jackson (JSON Processing)
Prerequisites
Java 21 or higher
Maven 3.6+

Installation
Clone the Repository



Update the application.properties file in src/main/resources with your database credentials:

properties
Copy code
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdbname
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
Build the Project

bash
Copy code
mvn clean install
Running the Application
bash
Copy code
mvn spring-boot:run
The application will start on http://localhost:8080.

API Endpoints
Create a Payment
URL: /payments

Method: POST

Headers:

Content-Type: application/json
X-Forwarded-For: <client-ip-address> (Optional)
Request Body:

json
Copy code
{
"amount": 150.75,
"debtorIban": "LT121000011101001000"
}
Response:

Status: 201 Created
Body: Created Payment object.
Upload Payments File
URL: /payments/payment-files

Method: POST

Headers:

Content-Type: multipart/form-data
X-Forwarded-For: <client-ip-address> (Optional)
Form Data:

file: CSV file with headers amount, debtorIban.
Response:

Status: 201 Created
Body: List of created Payment objects.
Retrieve Payments
URL: /payments

Method: GET

Query Parameters:

debtorIban (Optional): Filter payments by debtor IBAN.
Response:

Status: 200 OK
Body: List of Payment objects.
Validation
Amount: Must be a positive decimal number.
Debtor IBAN: Must be a valid IBAN from Lithuania (LT), Latvia (LV), or Estonia (EE).
CSV Upload: Each record is validated; invalid records are skipped.
Error Handling
Validation Errors: Returns 400 Bad Request with a JSON object containing field-specific error messages.

json
Copy code
{
"amount": "Amount must be a positive number",
"debtorIban": "Invalid Baltic IBAN"
}
Country Resolution
The CountryResolver class uses the ip-api.com service to determine the client's country based on their IP address. The country code is stored with each payment.

Logging
Uses SLF4J and Logback for logging errors and informational messages.

Testing
Run unit tests with:

bash
Copy code
mvn test