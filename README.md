# Backend Engineering Case Study

## Table of Contents
1. [Project Structure](#project-structure)
2. [Design Choices](#design-choices)
3. [Usage](#usage)
4. [Technologies Used](#additional-technologies)

## Project Structure
- **Controller:** Contains RESTful controller classes. These classes handle incoming HTTP requests and define the API endpoints.
- **Entity:** Stores data models that are mapped to database tables and define the properties and relationships of the application data.
- **Enums:** Used to represent pre-defined `Country` values.
- **Exception:** Contains custom exception classes for managing errors and exceptional conditions. These classes ensure consistent error handling and meaningful error messages throughout the application.
- **Repo:** Contains repository classes that deal with data access using `JPA (Java Persistence API)` to interact with the database.
- **Service:** Contains service classes that implement business logic. Controllers use these services to perform operations on data.

## Design Choices

![mysql-db](https://github.com/user-attachments/assets/4867d49c-e1ed-4f3b-9272-fa2b0570b548)

### For concurrency
I implemented locking mechanisms on `userId` and `country`. When a user updating their level or claiming reward, the operations are **synchronized** on the `userId`. This prevents multiple threads from modifying the same user's data simultaneously, ensuring data integrity. When a user enters a tournament, they are **synced** on the `country`, preventing multiple users from the same country from joining the same group.

### For performance 
I **cached the current tournament in a variable** instead of constantly querying the database. I also **used a `ConcurrentHashMap` for country scores**, allowing fast, **thread-safe updates**. This in-memory storage reduces latency compared to frequent database writes. I simply saved the HashMap to a `countryLeaderboard.dat` file at shutdowns. Then, I load HashMap from that file at application starts. A cron job saves Country Leaderboard to database at 20:00 UTC.


### Services 
- `TournamentService` enter tournament operations and country leaderboard managment for a tournament.
- `TournamentParticipantService` for participant operations such as claiming rewards, updating scores, learning their rankings.
- `UserService` for user actions such as creating user and updating level.
- `TournamentGroupService` for group leaderboard management.

This **`separation of concerns`** makes the code **easier to read and maintain**.

Custom exceptions like `NoActiveTournamentException` and `UserAlreadyParticipantException` help handle errors clearly and provide precise feedback to users.

When assigning tournament participants to their groups, I proceed by assigning them to the group closest to the start, that is, the fullest. 

TournamentParticipant createdAt attribute used in claimReward in order to get last participation. 


## Usage 
1. Clone the repository
2. Navigate to the project directory
3. Run `docker compose up`
4. Send Request to endpoints (postman collection provided)

## Additional Technologies
- **Spring Data JPA**
- **Lombok**
- **H2 (for testing)**
- **Mockito**
