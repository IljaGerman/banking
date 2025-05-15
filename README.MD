# Project Setup Guide

## Requirements

- Docker
- Docker Compose
- JDK 21 (Amazon Corretto 21)

## Architecture Overview

The system utilizes:

- **Redis** for bonus calculation caching and temporary storage of initial deposits
- **PostgreSQL** as the primary database
- **Spring Boot** as the application framework

## Key Features Implementation

### Bonus Accumulation System

✨ **Redis Integration**:

- ✅ Used to temporarily store initial deposit amounts while calculating bonuses

🔢 **Bonus Logic**:

- ⏳ Initial deposits are stored in Redis with TTL (Time-To-Live)
- 📈 Balance increases gradually with each qualifying transaction
- 🔍 System checks Redis for existing deposit records before applying bonuses

## Steps to Run the Project

1. Ensure that you have Docker, Docker Compose, and JDK 21 (Amazon Corretto 21) installed.

2. Build the project to generate the JAR file:

   mvn package

3. Build the Docker images:

   docker-compose build

4. Start the project using Docker Compose:

   docker-compose up

5. The project will be accessible at `http://localhost:8080`.

- To remove the containers:

  docker-compose down

### User Authentication

To authenticate in the system, you must provide **either an email or a phone number**. The credentials required for authentication are:

- **Email**  
  Used for logging into the system. Example: `ivanov.ii@example.com`

- **Phone**  
  Alternative login method. Example: `+79161234567`

#### Example authentication request:
```json
{
  "email": "ivanov.ii@example.com",
  "password": "12345"
}
```
or
```json
{
  "phone": "+79161234567",
  "password": "12345"
}
```

Authentication can be performed **using either an email or a phone number**, depending on user preference.  
When processing an authentication request, the system verifies the provided **login credential (email or phone)** and **password** against the database.
