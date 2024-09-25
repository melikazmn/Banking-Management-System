package org.example;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;

public class Methods {
    public static String loginUser(String username, String password,Connection connection) {
        String selectUserSQL = "SELECT * FROM user WHERE username = ? AND password = ?";
        SecureRandom random = new SecureRandom();
        StringBuilder tokenBuilder = new StringBuilder(12);

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successful. Welcome, " + resultSet.getString("firstName") + "!");

                while (true){
                    for (int i = 0; i < 12; i++) {
                        int randomIndex = random.nextInt(CHARACTERS.length());
                        char randomChar = CHARACTERS.charAt(randomIndex);
                        tokenBuilder.append(randomChar);
                    }
                    String updateTokenSQL = "UPDATE user SET token = ? WHERE username = ? AND password = ?";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateTokenSQL)) {
                        preparedStatement2.setString(1, tokenBuilder.toString());
                        preparedStatement2.setString(2, username);
                        preparedStatement2.setString(3, password);

                        preparedStatement2.executeUpdate();
                        System.out.println("Token updated successfully.");
                        break;
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println("Error: Unique constraint violation. Regenerating a new token and retrying...");
                    }
                }

            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokenBuilder.toString();
    }



    static void registerUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("User Registration");
        String username;
        while (true) {
            System.out.println(">> enter 0 if you want to exit ");
            System.out.println("Enter username: ");
            username = scanner.nextLine();
            if (Objects.equals(username, "0"))
                break;
            try {
                if (!isUsernameUnique(username, connection))
                    System.out.println("Username already exists. Please choose a different username.");
                else {
                    System.out.println("Enter password: ");
                    String password = scanner.nextLine();

                    System.out.println("Enter first name: ");
                    String firstName = scanner.nextLine();

                    System.out.println("Enter last name: ");
                    String lastName = scanner.nextLine();

                    System.out.println("Enter birth date (YYYY-MM-DD): ");
                    String birthDate = scanner.nextLine();

                    System.out.println("Enter phone number: ");
                    String phoneNumber = scanner.nextLine();

                    System.out.println("Enter registration date (YYYY-MM-DD): ");
                    String createdAt = scanner.nextLine();

                    System.out.println("Enter role: ");
                    String role = scanner.nextLine();

                    String insertUserQuery = "INSERT INTO User (username, password, createdAt, firstName, lastName, birthDate, phoneNumber, role) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery)) {
                        preparedStatement.setString(1, username);
                        preparedStatement.setString(2, password);
                        preparedStatement.setDate(3, Date.valueOf(createdAt));
                        preparedStatement.setString(4, firstName);
                        preparedStatement.setString(5, lastName);
                        preparedStatement.setDate(6, Date.valueOf(birthDate));
                        preparedStatement.setString(7, phoneNumber);
                        preparedStatement.setString(8, role);

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("User registered successfully.");
                        } else {
                            System.out.println("Failed to register user.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    private static boolean isUsernameUnique(String username, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE username = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        }
        return false;
    }


    public static void updateProfile(String token,Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(">>Enter 0 to exit");
            System.out.println("Enter new username: ");
            String newUsername = scanner.nextLine();
            if (Objects.equals(newUsername, "0"))
                break;
            if (!isUsernameUnique(newUsername, connection))
                System.out.println("Username already exists. Please choose a different username.");
            else {
                String query = "UPDATE user SET username = ? WHERE token = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, newUsername);
                    preparedStatement.setString(2, token);
                    int rowsUpdated  = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0)
                        System.out.println("Username updated successfully.");
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static boolean doesAccountExist(int accountNumber,Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Account WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    private static int findUserIdByToken(Scanner scanner,Connection connection,String token) {
        String query = "SELECT id FROM user WHERE token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                System.out.println("not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    static ArrayList<String> getAccountNamesByUserId(Connection connection, int userId) throws SQLException {
        ArrayList<String> accountNames = new ArrayList<>();
        String query = "SELECT name FROM account WHERE userId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                accountNames.add(resultSet.getString("name"));
            }
        }
        return accountNames;
    }

    static int getAccountNumber(String selectedAccountName, Connection connection, int userId) throws SQLException {
        String query = "SELECT accountNumber FROM Account WHERE name = ? AND userId = ?";
        int accountNumber = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, selectedAccountName);
            preparedStatement.setInt(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    accountNumber = resultSet.getInt("accountNumber");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return accountNumber;
    }

    static boolean transactionSetter(Connection connection, int accountNumber, String type, double amount, String date){
        String query = "INSERT INTO transaction (accountNumber, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setString(2, type);
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(amount));
            preparedStatement.setDate(4, Date.valueOf(date));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deposit(Scanner scanner,Connection connection,String token) throws SQLException {
        int userId = findUserIdByToken(scanner, connection,token);
        ArrayList<String> accountNames = getAccountNamesByUserId(connection, userId);
        System.out.println("Available Accounts:");
        for (int i = 0; i < accountNames.size(); i++)
            System.out.println((i + 1) + ". " + accountNames.get(i));
        System.out.println(">> Deposit");
        System.out.println("Enter number of account name: ");
        int Number = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        if (Number >= 1 && Number <= accountNames.size()) {
            String selectedAccountName = accountNames.get(Number - 1);
            int accNum = getAccountNumber(selectedAccountName,connection,userId );
            String updateQuery = "UPDATE account SET balance = balance + ? WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setBigDecimal(1, BigDecimal.valueOf(amount));
                preparedStatement.setInt(2, accNum);
                preparedStatement.executeUpdate();
                transactionSetter(connection,accNum,"deposit",amount,date);
                System.out.println("Deposit successful!");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Invalid account selection.");
        }
    }

    private static void withdraw(Scanner scanner,Connection connection,String token) throws SQLException {
        int userId = findUserIdByToken(scanner, connection,token);
        ArrayList<String> accountNames = getAccountNamesByUserId(connection, userId);
        System.out.println("Available Accounts:");
        for (int i = 0; i < accountNames.size(); i++)
            System.out.println((i + 1) + ". " + accountNames.get(i));
        System.out.println(">> withdraw");
        System.out.println("Enter number of account name: ");
        int Number = scanner.nextInt();
        System.out.println("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        if (Number >= 1 && Number <= accountNames.size()) {
            String selectedAccountName = accountNames.get(Number - 1);
            String query = "SELECT balance FROM account WHERE userId = ? AND name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, selectedAccountName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (currentBalance >= amount) {
                        int accNum = getAccountNumber(selectedAccountName,connection,userId );
                        String updateQuery = "UPDATE account SET balance = balance - ? WHERE accountNumber ?";
                        try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateQuery)) {
                            preparedStatement2.setDouble(1, amount);
                            preparedStatement2.setInt(2, accNum);
                            preparedStatement2.executeUpdate();
                            transactionSetter(connection,accNum,"Withdraw",amount,date);
                            System.out.println("Withdraw successful!");
                        }
                    }
                    else {
                        System.out.println("NOT enough money!!!!!!");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Invalid account selection.");
        }
    }

    private static void requestLoan(Scanner scanner,String token,Connection connection) throws SQLException {
        System.out.println("Loan Request");
        int userId = findUserIdByToken(scanner, connection,token);
        ArrayList<String> accountNames = getAccountNamesByUserId(connection, userId);
        System.out.println("Available Accounts:");
        for (int i = 0; i < accountNames.size(); i++)
            System.out.println((i + 1) + ". " + accountNames.get(i));
        System.out.println("Enter number of account name: ");
        int Number = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter amount of requested loan: ");
        int requestedAmount = scanner.nextInt();
        scanner.nextLine();
        String selectedAccountName = accountNames.get(Number - 1);
        System.out.print("Enter registration date (YYYY-MM-DD): ");
        String createdAt = scanner.nextLine();

        String query = "SELECT accountNumber FROM Account WHERE name = ? AND userId = ?";
        int accountNumber = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, selectedAccountName);
            preparedStatement.setInt(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    accountNumber = resultSet.getInt("accountNumber");
                }
            }
        }

        try {
            if (!isEligibleForLoan(userId,connection,selectedAccountName)) {
                System.out.println("User is not eligible for a loan.");
                return;
            }
            String insertLoanQuery = "INSERT INTO Loan (accountId, loanAmount, createdAt, status, userId) " +
                    "VALUES (?, ?, ?, 'Pending', ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertLoanQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, accountNumber);
                preparedStatement.setBigDecimal(2, java.math.BigDecimal.valueOf(requestedAmount));
                preparedStatement.setDate(3, Date.valueOf(createdAt));
                preparedStatement.setInt(4, userId);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int loanId = generatedKeys.getInt(1);
                        System.out.println("Loan request submitted successfully.");
                    }
                } else {
                    System.out.println("Failed to submit loan request.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isEligibleForLoan(int userId,Connection connection,String selectedAccountName) throws SQLException {
        String query = "SELECT accountNumber FROM Account WHERE name = ? AND userId = ?";
        int accountNumber = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, selectedAccountName);
            preparedStatement.setInt(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    accountNumber = resultSet.getInt("accountNumber");
                }
            }
        }
        String incomeQuery = "SELECT SUM(amount) as SM FROM Transaction " +
                "WHERE transactionDate BETWEEN '2024-01-01' AND '2024-02-01' " +
                "AND accountNumber = ?";
        try (PreparedStatement incomeStatement = connection.prepareStatement(incomeQuery)) {
            incomeStatement.setInt(1, accountNumber);
            ResultSet incomeResult = incomeStatement.executeQuery();
            if (incomeResult.next()) {
                double income = incomeResult.getDouble("SM");
                return income > 10000000;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void payLoan(Scanner scanner,Connection connection,int installmentId ,int loanId,String date) throws SQLException {
        String getAccountNumQ = "SELECT accountId FROM loan WHERE loanId  = ?";
        int accountNumber = 0 ;
        double balance = 0 ;
        double loanInstallmentAmount = 0;
        int rowCount =0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAccountNumQ)) {
            preparedStatement.setInt(1, loanId );
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accountNumber = resultSet.getInt("accountId");
            }
        }

        String getBalanceQ = "SELECT balance FROM account WHERE accountNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getBalanceQ)) {
            preparedStatement.setInt(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        }

        String loanInstallmentAmountQ = "SELECT amount FROM LoanInstallment WHERE installmentId  = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(loanInstallmentAmountQ)) {
            preparedStatement.setInt(1, installmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                loanInstallmentAmount = resultSet.getDouble("amount");
            }
        }

        if (balance >= loanInstallmentAmount) {
            String updateQuery = "UPDATE account SET balance = balance - ? WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateQuery)) {
                preparedStatement2.setDouble(1, loanInstallmentAmount);
                preparedStatement2.setInt(2, accountNumber);
                preparedStatement2.executeUpdate();
                transactionSetter(connection,accountNumber,"Withdraw",loanInstallmentAmount,date);
                System.out.println("Withdraw successful!");
            }
            System.out.println("Loan installment paid successfully.");
        } else {
            System.out.println("Insufficient balance to pay the loan installment.");
        }

    }

    public static void addLoanInstallment(Scanner scanner,Connection connection,String token) throws SQLException{
        int userId = findUserIdByToken(scanner,connection,token);
        System.out.println("All Approved Loans for this user:");
        String query = "SELECT * FROM loan where userId = ? AND status = 'Approved' ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
                 preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int loanId = resultSet.getInt("loanId");
                    int usrId = resultSet.getInt("userId");
                    int accountId = resultSet.getInt("accountId");
                    double loanAmount = resultSet.getDouble("loanAmount");
                    String status = resultSet.getString("status");
                    System.out.println("Loan ID: " + loanId +
                            ", User ID: " + usrId +
                            ", Account ID: " + accountId +
                            ", Loan Amount: " + loanAmount +
                            ", Status: " + status);
                }
                System.out.println("Enter the Loan ID to pay installment if it has: ");
                int inputLoanId = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                int rowCount = 0;
                String qury = "SELECT COUNT(*) FROM loan WHERE loanId = ?";
                try (PreparedStatement preparedStatement2 = connection.prepareStatement(qury)) {
                    preparedStatement2.setInt(1, inputLoanId);

                    try (ResultSet restSet = preparedStatement2.executeQuery()) {
                        if (restSet.next()) {
                            rowCount = restSet.getInt(1);
                        }
                    }
                }
                String q = "SELECT installmentId FROM loaninstallment WHERE loanId = ? ORDER BY dueDate DESC LIMIT 1";
                try (PreparedStatement preparedStatement3 = connection.prepareStatement(q)) {
                    preparedStatement3.setInt(1, inputLoanId);
                    ResultSet results = preparedStatement3.executeQuery();  // Use preparedStatement3
                    if (results.next()) {
                        payLoan(scanner, connection, results.getInt("installmentId"), inputLoanId, date);
                        String updateQuery = "UPDATE loaninstallment SET status = 'Paid' WHERE installmentId  = ?";
                        try (PreparedStatement preparedStatement4 = connection.prepareStatement(updateQuery)) {
                            preparedStatement4.setInt(1, results.getInt("installmentId"));
                            preparedStatement4.executeUpdate();
                        }
                        if (rowCount + 1 == 12){
                            String updateloanQ = "UPDATE Loan SET status = 'finished' WHERE loanId   = ?";
                            try (PreparedStatement preparedStatement5 = connection.prepareStatement(updateloanQ)) {
                                preparedStatement5.setInt(1, inputLoanId);
                                preparedStatement5.executeUpdate();
                            }
                        }
                        else {
                            String qs = "SELECT * FROM loaninstallment WHERE loanId = ? ORDER BY dueDate DESC LIMIT 1";
                            try (PreparedStatement preparedStatement4 = connection.prepareStatement(qs)) {
                                preparedStatement4.setInt(1, inputLoanId);
                                ResultSet fullResult = preparedStatement4.executeQuery();
                                if (fullResult.next()) {
                                    String nextDueDate = fullResult.getString("dueDate");
                                    String paymentDate = fullResult.getString("paymentDate");
                                    BigDecimal amount = fullResult.getBigDecimal("amount");
                                    Calendar calendar = Calendar.getInstance();
                                    java.util.Date DueD = new SimpleDateFormat("yyyy-MM-dd").parse(nextDueDate);
                                    calendar.setTime(DueD);
                                    // Increment by 1 month for dueDate
                                    calendar.add(Calendar.MONTH, 1);
                                    java.util.Date dueDate = calendar.getTime();
                                    String insertQ = "INSERT INTO LoanInstallment (loanId, amount, dueDate, paymentDate, status) VALUES (?,?,?,?, 'PENDING')";
                                    try (PreparedStatement preparedStatement8 = connection.prepareStatement(insertQ)) {
                                        preparedStatement8.setInt(1, inputLoanId);
                                        preparedStatement8.setBigDecimal(2, amount);
                                        java.util.Date utilDate = dueDate;
                                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                                        preparedStatement8.setDate(3, sqlDate);
                                        preparedStatement8.setDate(4, Date.valueOf(paymentDate));
                                        preparedStatement8.executeUpdate();
                                    }
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

            }
        }



    }



    public static void userMenu(String token,Connection connection) throws SQLException {
        while (true) {
            System.out.println("\n>> USER Menu");
            System.out.println("1. Update Profile");
            System.out.println("2. Deposit ");
            System.out.println("3. Withdraw");
            System.out.println("4. Loan Request");
            System.out.println("5. Loan installment pay");
            System.out.println("0. Exit");
            System.out.println("Choose an option: ");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    updateProfile(token,connection);
                    break;
                case 2:
                    deposit(scanner,connection,token);
                    break;
                case 3:
                    withdraw(scanner,connection,token);
                    break;
                case 4:
                    requestLoan(scanner,token,connection);
                    break;
                case 5:
                    addLoanInstallment(scanner,connection,token);
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
