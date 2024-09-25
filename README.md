# Banking Management System

## Overview
This project is a Banking Management System implemented in Java, using SQL as the database backend. It supports multiple functionalities such as user registration, login, transactions (withdrawals and deposits), loan management, and account balance updates. The system is designed for both customers and employees, with the latter having additional privileges like approving loans and conducting transactions on behalf of customers.

## Features
1. User Management
- Registration: Allows new users to register by entering their details such as username, password, name, birth date, phone number, and role (customer/employee).
- Login: Users can log in to the system by providing their username and password. A secure token is generated and stored in the database upon successful login.
2. Transactions
- Withdrawals and Deposits: Employees can facilitate money transfers between accounts by handling withdrawals and deposits. Both customers and employees can make direct transactions.
- Transaction Logging: Every transaction is logged in the database with the associated details like amount and date.
3. Loan Management
- Loan Status Check: Employees can view all existing loans, including details such as loan ID, user ID, account ID, loan amount, and loan status.
- Loan Approval: Employees can approve or reject loans. Upon approval, the loan amount is deposited into the user's account, and the first loan installment is scheduled.
4. Secure Token-based Authentication
- After a successful login, the system generates a secure token using the SecureRandom class, ensuring a unique session token for each login. This token is then stored in the database for future authentication checks.

## Database Structure
The following database tables are utilized:

- User: Stores user information including username, password, first name, last name, birth date, phone number, role, and login token.
- Account: Stores account information like account number and balance.
- Loan: Contains information about loans, including loan ID, account ID, user ID, loan amount, and status.
- Transaction: Logs all transactions (withdrawals and deposits) made by users.
- LoanInstallment: Manages the installment details of loans, including due dates and payment dates.

## How to Use
1. Employee Menu
The employee menu provides options to:

- Create Account: Employees can register new users in the system.
- Do Transaction: Employees can initiate transactions on behalf of users, such as withdrawals and deposits.
- Loan Check: Employees can view all loans and modify their status (Pending, Approved, Rejected, or Finished).
2. Transaction Flow
When an employee initiates a transaction:

- They first enter the username of the withdrawing and depositing users.
- The system then retrieves available accounts and performs the transaction, updating the account balances accordingly.
- The transaction is logged in the database.
3. Loan Approval Flow
When a loan is approved:

- The loan amount is added to the user's account balance.
- The first loan installment is scheduled, with the due date set to one month from the approval date and the payment date one year later.

## Prerequisites
- Java: Ensure you have Java installed (Java 8 or later).
- SQL Database: Set up a relational database system (e.g., MySQL or PostgreSQL) and create the necessary tables as per the structure mentioned above.
