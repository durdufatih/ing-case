# Loan Case Application

A Spring Boot application for managing loan applications with Spring Web, H2 database, and Spring Data JPA.

## Features

- RESTful API for loan management
- In-memory H2 database
- Spring Data JPA for data access
- Sample data initialization

## Technologies

- Java 17
- Spring Boot 3.2.1
- Spring Web
- Spring Data JPA
- H2 Database

## Project Structure

```
src/main/java/com/example/loancase/
├── config/
│   └── DataInitializer.java
├── controller/
│   ├── HomeController.java
│   └── LoanController.java
├── model/
│   └── Loan.java
├── repository/
│   └── LoanRepository.java
├── service/
│   └── LoanService.java
└── LoanCaseApplication.java
```

## Running the Application

1. Make sure you have Java 17 installed
2. Clone the repository
3. Navigate to the project directory
4. Run the application using Maven:
   ```
   ./mvnw spring-boot:run
   ```
   or
   ```
   mvn spring-boot:run
   ```

The application will start on port 8080 by default.

### Running in Production Mode

To run the application in production mode, specify the `prod` profile:
This prod env run application with ddl none mode you have to create all tables before running
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
or
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Running Tests

To run the tests, use the following Maven command:
```
./mvnw test
```
or
```
mvn test
```

## API Endpoints

### Home
- `GET /` - Welcome message

### Loan Management
- \`GET /api/loans\` \- List loans (with optional filters)
- \`POST /api/loans\` \- Create a new loan

### Installment Management- 
- \`GET /api/installments/loan/{loanId}\` \- List installments for a loan
- \`POST /api/installments/loan/{loanId}?amount={amount}\` \- Pay installments for a loan

## H2 Database Console

The H2 console is enabled and can be accessed at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:loandb`
- Username: `sa`
- Password: `password`

## Sample Data

The application is initialized with sample loan data:
- John Doe: $10,000 loan for 12 months at 5.5% interest (PENDING)
- Jane Smith: $25,000 loan for 24 months at 4.75% interest (APPROVED)
- Michael Johnson: $5,000 loan for 6 months at 6.25% interest (REJECTED)
- Sarah Williams: $15,000 loan for 18 months at 5.0% interest (APPROVED)

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix:
   ```
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```
   git commit -m "Description of changes"
   ```
4. Push to your branch:
   ```
   git push origin feature-name
   ```
5. Open a pull request on GitHub.

Please ensure your code follows the project's coding standards and includes appropriate tests.
`