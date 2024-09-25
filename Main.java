package org.example;

import java.sql.*;
import java.text.ParseException;
import java.util.Scanner;

import static org.example.AdminService.adminMenu;
import static org.example.Employee.employeeMenu;
import static org.example.Methods.*;
import static sun.security.jgss.GSSUtil.login;

public class Main {
    public static void main(String[] args) throws SQLException, ParseException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
        while (true) {
            System.out.println("\nLog In To Bank Management System");
            String userToken;
            System.out.println(">> USERNAME:");
            String username = scanner.nextLine();
            System.out.println(">> PASSWORD:");
            String password = scanner.nextLine();
            userToken = loginUser(username,password,connection);

            String selectRoleSQL = "SELECT role FROM user WHERE token = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectRoleSQL);
            preparedStatement.setString(1, userToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                switch (role){
                    case "admin":
                        adminMenu(connection);
                        break;
                    case "user":
                        userMenu(userToken,connection);
                        break;
                    case "employee":
                        employeeMenu(connection);
                        break;
                }
            }
            }
        }

}
