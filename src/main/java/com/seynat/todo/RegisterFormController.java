package com.seynat.todo;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class  RegisterFormController {
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnRegister;
    public Label lblIncorrect1;
    public Label lblIncorrect2;
    public TextField txtEmail;
    public TextField txtUsername;
    public Label lblID;
    public AnchorPane root;

    public void initialize(){
        setLblVisibility(false);
        setDisableCommon(true);

    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        register();
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        register();

    }
    public void register(){
        String newPassword = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String id = lblID.getText();
        


        if(newPassword.equals(confirmPassword)){
            setBorderColour("transparent");
            setLblVisibility(false);
            System.out.println("Registered");

        } else {
            setBorderColour("red");
            txtPassword.requestFocus();
            setLblVisibility(true);
        }
        
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("insert into user values (?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,username);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,confirmPassword);

            int i = preparedStatement.executeUpdate();

            if(i>0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User Registered Successfully", ButtonType.OK);
                alert.show();

                FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("Login.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Home!");
                primaryStage.centerOnScreen();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "User Registration Failed", ButtonType.OK);
                alert.show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void setLblVisibility(boolean isVisible){
        lblIncorrect1.setVisible(isVisible);
        lblIncorrect2.setVisible(isVisible);
    }
    public void setBorderColour(String color){
        txtPassword.setStyle("-fx-border-color: "+color);
        txtConfirmPassword.setStyle("-fx-border-color:"+color);
    }


    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);
        autoGenerateID();
    }
    public void setDisableCommon(boolean isDisable){
        txtUsername.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);

        Connection connection =DBConnection.getInstance().getConnection();
        System.out.println(connection);
    }

    public void autoGenerateID(){
      Connection connection = DBConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT id from user ORDER BY id DESC LIMIT 1");
            boolean isExist = resultSet.next();

            if(isExist){
                String oldID = resultSet.getString(1);

                int substring = Integer.parseInt(oldID.substring(1, 4));
                int newID = substring + 1;
                if(newID < 10){
                    lblID.setText("U00"+newID);
                }else if(newID < 100){
                    lblID.setText("U0"+newID);
                }else if(newID < 1000){
                    lblID.setText("U"+newID);
                }
            }else {
                lblID.setText("U001");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
