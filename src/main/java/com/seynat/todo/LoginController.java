package com.seynat.todo;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    public Label lblRegister;
    public AnchorPane root;
    public TextField txtUsername;
    public TextField txtPassword;
    public Button btnLogin;
    public static String loginUsername;
    public static String loginId;

    public void lblRegisterOnMouseClick(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("RegisterForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register!");
        primaryStage.centerOnScreen();
        //show eke danne na.. eke paarai show eke
    }

    public void txtPasswordOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        login();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        login();
    }

    public void login() throws SQLException, IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from user where user_name = ? and password = ?");
        preparedStatement.setObject(1, username);
        preparedStatement.setObject(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){

            loginUsername = resultSet.getString(2);
            loginId = resultSet.getString(1);

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("Home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Todo!");
            primaryStage.centerOnScreen();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password", javafx.scene.control.ButtonType.OK);
            alert.show();

            txtUsername.clear();
            txtPassword.clear();
            txtUsername.requestFocus();

        }

    }
}
