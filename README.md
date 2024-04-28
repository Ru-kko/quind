
# Overview
This project is a financial services application that allows for the management of clients, accounts, and transactions through a RESTful API. The application is developed using Java and follows a hexagonal architecture. It can be run using Docker Compose.

# Features
## Clients
- Create, update, and delete clients.
- Client attributes: ID, identification type, identification number, first name, last name, email, date of birth, creation date, modification date.
- Automatic calculation of creation and modification dates.
- Restrictions: Clients must be of legal age and cannot be deleted if they have linked products.
## Accounts (Products)
- Create two types of accounts: current and savings.
- Account attributes: ID, account type, account number, status (active, inactive, cancelled), balance, creation date, modification date, linked client.
- Automatic generation of unique 10-digit numeric account numbers. Savings accounts start with “53”, current accounts start with “33”.
- Restrictions: Savings accounts cannot have a negative balance. Accounts can only be cancelled if the balance is $0.
## Transactions
- Create three types of transactions: deposit, withdrawal, and transfer between accounts.
- Update account balance and available balance with each successful transaction.
# Non-Functional Requirements
- The backend project is developed using Java and springboot.
- The project follows a hexagonal architecture.
- The project can be run using Docker Compose.
- Automate migration manage with FLyWay

# Running the Project
To run the project, use the following command:

```docker-compose up```

Please note that you need to have Docker installed on your machine to run the project.