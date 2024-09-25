package org.example;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import static org.example.Methods.*;

public class Employee {
    public static int getIdByUserName(Connection connection,String username) throws SQLException {
        int userId;
        String query = "SELECT id FROM user WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                    return userId;
                }
                else{
                    System.out.println("Wrong username");
                    return -1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public static boolean transactionByEmployee(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username of who wants to withdraw:");
        String usernameWithdraw = scanner.nextLine();
        int userIdWithdraw = getIdByUserName(connection,usernameWithdraw);
        ArrayList<String> accountNamesWithdraw = getAccountNamesByUserId(connection, userIdWithdraw);
        System.out.println("Available Accounts:");
        for (int i = 0; i < accountNamesWithdraw.size(); i++)
            System.out.println((i + 1) + ". " + accountNamesWithdraw.get(i));
        System.out.println("Enter number of account name: ");
        int NumberW = scanner.nextInt();
        scanner.nextLine();


        System.out.println("Enter username of who wants to deposit:");
        String usernameDeposit = scanner.nextLine();
        int userIdDeposit = getIdByUserName(connection,usernameDeposit);
        ArrayList<String> accountNamesDeposit = getAccountNamesByUserId(connection, userIdDeposit);
        System.out.println("Available Accounts:");
        for (int i = 0; i < accountNamesDeposit.size(); i++)
            System.out.println((i + 1) + ". " + accountNamesDeposit.get(i));
        System.out.println("Enter number of account name: ");
        int NumberD = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter amount of transaction: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();


        String selectedAccountNameWitdraw = accountNamesWithdraw.get(NumberW - 1);
        String selectedAccountNameDeposit = accountNamesDeposit.get(NumberD - 1);
        int accNumWithdraw = getAccountNumber(selectedAccountNameWitdraw,connection,userIdWithdraw );
        int accNumDeposit = getAccountNumber(selectedAccountNameDeposit,connection,userIdDeposit );
        String updateQuery = "UPDATE account SET balance = balance - ? WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateQuery)) {
            preparedStatement2.setBigDecimal(1, BigDecimal.valueOf(amount));
            preparedStatement2.setInt(2, accNumWithdraw);
            preparedStatement2.executeUpdate();
            transactionSetter(connection,accNumWithdraw,"Withdraw",amount,date);
            System.out.println("Withdraw successful!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateQuery2)) {
            preparedStatement2.setBigDecimal(1, BigDecimal.valueOf(amount));
            preparedStatement2.setInt(2, accNumDeposit);
            preparedStatement2.executeUpdate();
            transactionSetter(connection,accNumDeposit,"deposit",amount,date);
            System.out.println("Deposit successful!");
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void addLoanToAccount(int loanId,Connection connection,Scanner scanner) throws SQLException {
        String query = "SELECT * FROM loan where loanId = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, loanId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int accountId = resultSet.getInt("accountId");
                    int loanAmount = resultSet.getInt("loanAmount");
                    System.out.println("Enter date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    String updateQuery = "UPDATE account SET balance = balance + ? WHERE accountNumber = ?";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateQuery)) {
                        preparedStatement2.setDouble(1, loanAmount);
                        preparedStatement2.setInt(2, accountId);
                        preparedStatement2.executeUpdate();
                        transactionSetter(connection,accountId,"deposit",loanAmount,date);
                        System.out.println("Deposit successful!");
                    }
                } else{
                    System.out.println("Wrong account");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void addFirstLoanInstallmentDue(int loanId,Connection connection,Scanner scanner) throws SQLException, ParseException {
        System.out.println("Add First LoanInstallment Due ");
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        String query = "SELECT * FROM loan where loanId = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, loanId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int loanAmount = resultSet.getInt("loanAmount");
                    float LoanInstallmentAmount = (float) (loanAmount/12.0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    // Increment by 1 month for dueDate
                    calendar.add(Calendar.MONTH, 1);
                    Date dueDate = calendar.getTime();
                    // Increment by 1 year for paymentDate
                    calendar.add(Calendar.YEAR, 1);
                    Date paymentDate = calendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String qry = "INSERT INTO LoanInstallment (loanId, dueDate, paymentDate, amount, status ) VALUES (?, ?, ?, ?,'Pending')";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(qry)) {
                        preparedStatement2.setInt(1, loanId);
                        preparedStatement2.setString(2, dateFormat.format(dueDate));
                        preparedStatement2.setString(3, dateFormat.format(paymentDate));
                        preparedStatement2.setBigDecimal(4, BigDecimal.valueOf(LoanInstallmentAmount));
                        preparedStatement2.executeUpdate();
                    }
                } else{
                    System.out.println("Wrong account");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void loanCheck(Scanner scanner,Connection connection) throws SQLException, ParseException {
        System.out.println("All Loans:");
        String query = "SELECT * FROM loan";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int loanId = resultSet.getInt("loanId");
                int userId = resultSet.getInt("userId");
                int accountId = resultSet.getInt("accountId");
                double loanAmount = resultSet.getDouble("loanAmount");
                String status = resultSet.getString("status");
                System.out.println("Loan ID: " + loanId +
                        ", User ID: " + userId +
                        ", Account ID: " + accountId +
                        ", Loan Amount: " + loanAmount +
                        ", Status: " + status);
            }
        }
        System.out.println("Enter the Loan ID to reset its status: ");
        int lnId = scanner.nextInt();
        scanner.nextLine();
        String newStatus = new String("pending");
        System.out.println("Enter the new status for the loan : ");
        System.out.println("1. Pending");
        System.out.println("2. Approved");
        System.out.println("3. Rejected");
        System.out.println("4. finished");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice){
            case 1:
                newStatus = "pending";
                break;
            case 2:
                newStatus = "approved";
                addLoanToAccount(lnId,connection,scanner);
                addFirstLoanInstallmentDue(lnId,connection,scanner);
                break;
            case 3:
                newStatus = "rejected";
                break;
            case 4:
                newStatus = "finished";
                break;
        }
        String updateQuery = "UPDATE loan SET status = ? WHERE loanId = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, newStatus);
            updateStatement.setInt(2, lnId);
            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Loan status updated successfully.");
            } else {
                System.out.println("Failed to update loan status. Loan ID may be incorrect.");
            }
        }
    }


    public static void employeeMenu(Connection connection) throws SQLException, ParseException {
        while (true) {
            System.out.println("\n>> Employee Menu");
            System.out.println("1. Create Account");
            System.out.println("2. Do Transaction ");
            System.out.println("3. Loan Check");
            System.out.println("0. Exit");
            System.out.println("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    registerUser(connection);
                    break;
                case 2:
                    transactionByEmployee(connection);
                    break;
                case 3:
                    loanCheck(scanner,connection);
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
