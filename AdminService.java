package org.example;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static org.example.Methods.registerUser;
import static org.example.Methods.transactionSetter;

public class AdminService {
    private static void createLoan(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO loan (userId, accountId, loanAmount, status, createdAt) " +
                        "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 1);
            preparedStatement.setBigDecimal(3, new BigDecimal("10000.00"));
            preparedStatement.setString(4, "Pending");
            preparedStatement.setString(5, "2022-06-10");

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int loanId = generatedKeys.getInt(1);
                    }
                }
            }

        }
    }


    private static ArrayList<Integer> getUserId(Connection connection,Scanner scanner) throws SQLException {
        ArrayList<Integer> res = new ArrayList();
        String query = "SELECT id FROM user";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(resultSet.getInt("id"));
            }
        }
        return res;
    }

    private static void createAccount(Connection connection,Scanner scanner) throws SQLException {
        System.out.println("Create Account");
        ArrayList<Integer> userIds = getUserId(connection,scanner);
        readUser(connection);
        System.out.println("Which user do you want to add account?");
        int userId = scanner.nextInt();
        scanner.nextLine();
        userId = userIds.get(userId-1);
        System.out.println("Account name?");
        String name = scanner.nextLine();

        try {
            String insertAccountQuery = "INSERT INTO Account (balance, name, userId, createdAT) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(insertAccountQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, 0);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3, userId);
                preparedStatement.setDate(4, Date.valueOf("2024-02-01"));
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int loanId = generatedKeys.getInt(1);
                        System.out.println("account creation submitted successfully.");
                    }
                } else {
                    System.out.println("Failed to submit account request.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void searchUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Search BY: ");
        System.out.println("1. PhoneNumber ");
        System.out.println("2. Username ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 2) {
            System.out.println("Enter username: ");
            String username = scanner.nextLine();
            System.out.println("filtered by username:");
            String query = "SELECT * FROM user WHERE username like ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + username + "%");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String usrname = resultSet.getString("username");
                        String createdAt = resultSet.getString("createdAt");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        String birthDate = resultSet.getString("birthDate");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String role = resultSet.getString("role");
                        System.out.println(" ID: " + id +
                                ", Username: " + usrname +
                                ", createdAt : " + createdAt +
                                ", firstName: " + firstName +
                                ", lastName: " + lastName +
                                ", birthDate: " + birthDate +
                                ", phoneNumber: " + phoneNumber +
                                ", role: " + role);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (choice == 1) {
            System.out.println("Enter PhoneNumber: ");
            String phNumber = scanner.nextLine();
            System.out.println("filtered by PhoneNumber:");
            String query = "SELECT * FROM user WHERE phoneNumber like ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + phNumber + "%");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String usrname = resultSet.getString("username");
                        String createdAt = resultSet.getString("createdAt");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        String birthDate = resultSet.getString("birthDate");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String role = resultSet.getString("role");
                        System.out.println(" ID: " + id +
                                ", Username: " + usrname +
                                ", createdAt : " + createdAt +
                                ", firstName: " + firstName +
                                ", lastName: " + lastName +
                                ", birthDate: " + birthDate +
                                ", phoneNumber: " + phoneNumber +
                                ", role: " + role);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void searchAccount(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Search BY: ");
        System.out.println("1. accountNumber ");
        System.out.println("2. userId ");
        int choice = scanner.nextInt();
        if (choice == 1) {
            System.out.println("Enter accountNumber: ");
            int accountNumber = scanner.nextInt();
            String query = "SELECT * FROM Account WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, accountNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int accNumber = resultSet.getInt("accountNumber");
                        int balance = resultSet.getInt("balance");
                        String name = resultSet.getString("name");
                        int userId  = resultSet.getInt("userId");
                        String createdAt = resultSet.getString("createdAt");
                        System.out.println(" accountNumber: " + accNumber +
                                ", balance: " + balance +
                                ", name : " + name +
                                ", userId: " + userId +
                                ", createdAt: " + createdAt);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (choice == 2) {
            System.out.println("Enter UserId: ");
            int usrId = scanner.nextInt();
            String query = "SELECT * FROM Account WHERE userId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, usrId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        int accNumber = resultSet.getInt("accountNumber");
                        int balance = resultSet.getInt("balance");
                        String name = resultSet.getString("name");
                        int userId  = resultSet.getInt("userId");
                        String createdAt = resultSet.getString("createdAt");
                        System.out.println(" accountNumber: " + accNumber +
                                ", balance: " + balance +
                                ", name : " + name +
                                ", userId: " + userId +
                                ", createdAt: " + createdAt);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void sortLoanInstallments(Connection connection) throws SQLException {
        System.out.println("Sorted by date: ");
        String query = "SELECT * FROM LoanInstallment ORDER BY dueDate";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int installmentId  = resultSet.getInt("installmentId");
                    int loanId  = resultSet.getInt("loanId");
                    int amount  = resultSet.getInt("amount");
                    String dueDate = resultSet.getString("dueDate");
                    String paymentDate = resultSet.getString("paymentDate");
                    String status  = resultSet.getString("status");
                    System.out.println(" installmentId : " + installmentId  +
                            ", loanId : " + loanId  +
                            ", amount : " + amount +
                            ", dueDate: " + dueDate +
                            ", paymentDate: " + paymentDate +
                            ", status: " + status);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static void deleteUser(Connection connection,Scanner scanner) throws SQLException{
        readUser(connection);
        System.out.println("Which user id do you want to delete?");
        int userId = scanner.nextInt();
        scanner.nextLine();
        String query = "Delete from user where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private static void readUser(Connection connection) throws SQLException {
        System.out.println("Read User");
        String query = "Select * From user";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String Date = resultSet.getString("createdAT");
                    String fName = resultSet.getString("firstname");
                    String lName = resultSet.getString("lastname");
                    String bDate = resultSet.getString("birthDate");
                    String num = resultSet.getString("phoneNumber");
                    String role = resultSet.getString("role");

                    System.out.println("user ID: " + id +
                            ", Username: " + username +
                            ", password: " + password +
                            ", Created Date: " + Date +
                            ", First Name: " + fName +
                            ", Last Name: " + lName +
                            ", Birthdate: " + bDate +
                            ", Phone Number: " + num +
                            ", Role: " + role);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void readLoanInst(Connection connection) throws SQLException {
        System.out.println("Here's the Loan Installments information");
        String query = "Select * From LoanInstallment";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    int id = resultSet.getInt("installmentId");
                    int loanID = resultSet.getInt("loanId");
                    int amount = resultSet.getInt("amount");
                    String Date = resultSet.getString("dueDate");
                    String pDate = resultSet.getString("paymentDate");
                    String status = resultSet.getString("status");


                    System.out.println("Installment ID: " + id +
                            ", Loan ID: " + loanID +
                            ", amount: " + amount +
                            ", Due Date: " + Date +
                            ", Payment Date: " + pDate +
                            ", status: " + status);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void readLoan(Connection connection) throws SQLException {
        System.out.println("Here's the Loans information");
        String query = "Select * From loan";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int loanId = resultSet.getInt("loanId");
                    int usrId = resultSet.getInt("userId");
                    int accountId = resultSet.getInt("accountId");
                    double loanAmount = resultSet.getDouble("loanAmount");
                    String status = resultSet.getString("status");
                    String Date = resultSet.getString("createdAT");
                    System.out.println("Loan ID: " + loanId +
                            ", User ID: " + usrId +
                            ", Account ID: " + accountId +
                            ", Loan Amount: " + loanAmount +
                            ", Created Date: " + Date +
                            ", Status: " + status);
                }
            }catch (SQLException exception) {
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void readAccount(Connection connection) throws SQLException {
        System.out.println("Here's the Accounts information");
        String query = "Select * From account";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("accountNumber");
                    double balance = resultSet.getDouble("balance");
                    String name = resultSet.getString("name");
                    int usrId = resultSet.getInt("userId");
                    String Date = resultSet.getString("createdAT");
                    System.out.println("Account ID: " + id +
                            ", User ID: " + usrId +
                            ", Account Balance: " + balance +
                            ", Account Name: " + name +
                            ", Created Date: " + Date);
                }
            }catch (SQLException exception) {
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void adminMenu(Connection connection) throws SQLException {
        while (true) {
            System.out.println("\n>> ADMIN Menu");
            System.out.println("1. create ");
            System.out.println("2. read ");
            System.out.println("3. update ");
            System.out.println("4. delete ");
            System.out.println("5. search users");
            System.out.println("6. search an account");
            System.out.println("7. sort the loan installments by date");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            int secondOption;
            switch (option) {
                case 1:
                    System.out.println("1. create loan");
                    System.out.println("2. create user ");//darim
                    System.out.println("3. create account ");
                    System.out.println("4. back");
                    secondOption = scanner.nextInt();
                    switch (secondOption) {
                        case 1:
                            createLoan(connection);
                            break;
                        case 2:
                            registerUser(connection);
                            break;
                        case 3:
                            createAccount(connection,scanner);
                            break;
                        case 4:
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    System.out.println("1. read loan");
                    System.out.println("2. read loan installment ");
                    System.out.println("3. read user ");
                    System.out.println("4. read account ");
                    System.out.println("5. back");
                    secondOption = scanner.nextInt();
                    switch (secondOption) {
                        case 1:
                            readLoan(connection);
                            break;
                        case 2:
                            readLoanInst(connection);
                            break;
                        case 3:
                            readUser(connection);
                            break;
                        case 4:
                            readAccount(connection);
                            break;
                        case 5:
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                    //update
                    break;
                case 4:
                    deleteUser(connection,scanner);
                    break;
                case 5:
                    searchUser(connection);
                    break;
                case 6:
                    searchAccount(connection);
                    break;
                case 7:
                    sortLoanInstallments(connection);
                    break;
                case 0:
                    System.out.println("Exiting Bank Management System.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }






}
