package com.seynat.todo;

import db.DBConnection;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class HomeController {
    public Label lblTitle;
    public Label lblId;
    public Button btnLogout;
    public AnchorPane root;
    public TextField txtSubGroup;
    public Button btnSubGroup;
    public Pane subGroup;
    public Button btnAddNewTodo;
    public ListView<ToDoTM> listTodo;
    public TextField txtUpdateDelete;
    public Button btnDelete;
    public Button btnUpdate;

    public void initialize(){
        lblTitle.setText("Welcome " + LoginController.loginUsername+ ", Todo List");
        lblId.setText(LoginController.loginId);

        subGroup.setVisible(false);
        loadList();
        btnUpdateDeleteDesable(true);
        listTodo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observableValue, ToDoTM toDoTM, ToDoTM newValue) {
                btnUpdateDeleteDesable(false);
                subGroup.setVisible(false);

                ToDoTM selectedItem = listTodo.getSelectionModel().getSelectedItem();
                if(selectedItem==null){
                    return;
                }
                txtUpdateDelete.setText(selectedItem.getDescription());
            }
        });
    }

    public void btnUpdateDeleteDesable(boolean isDisable){
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
        txtUpdateDelete.setDisable(isDisable);
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Logout ? ", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login!");
            primaryStage.centerOnScreen();

        }


    }

    public void btnSubGroupOnAction(ActionEvent actionEvent) {
        String description = txtSubGroup.getText();
        String userId = lblId.getText();
        String id = autoIdGenerate();

        Connection conn = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("insert into todolist values (?, ?, ?)");
            //Statement statement = conn.prepareStatement("insert into todolist values (?, ?, ?, ?)");
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, description);
            preparedStatement.setObject(3, userId);

            int i = preparedStatement.executeUpdate();

            if(i>0){
                System.out.println("Added");
                txtSubGroup.clear();
            } else {
                System.out.println("Not Added");
            }

            loadList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnAddNewTodoOnAction(ActionEvent actionEvent) {
        subGroup.setVisible(true);
        txtSubGroup.requestFocus();

        listTodo.getSelectionModel().clearSelection();
        btnUpdateDeleteDesable(true);
        txtUpdateDelete.clear();
    }

    public String autoIdGenerate(){
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todolist order by id desc limit 1");
            if(resultSet.next()){
                String oldId = resultSet.getString(1);
                String substring = oldId.substring(1, oldId.length());
                int id = Integer.parseInt(substring);
                id++;
                if(id<10){
                    return "T00"+id;
                } else if(id<100){
                    return "T0"+id;
                } else {
                    return "T"+id;
                }
            } else {
                return "T001";
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public void loadList(){
        ObservableList<ToDoTM> items = listTodo.getItems();
        items.clear();

        Connection conn = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from todolist where user_id = ?");
            preparedStatement.setObject(1, LoginController.loginId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id, description, userId);
                items.add(toDoTM);
            }
            listTodo.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void txtUpdateDeleteOnAction(ActionEvent actionEvent) {
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        ToDoTM selectedItem = listTodo.getSelectionModel().getSelectedItem();
        if(selectedItem==null){
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this item ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection conn = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = conn.prepareStatement("delete from todolist where id = ?");
                preparedStatement.setObject(1, selectedItem.getId());

                int i = preparedStatement.executeUpdate();
                if(i>0){
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully", ButtonType.OK);
                    alert1.show();
                    loadList();
                    btnUpdateDeleteDesable(true);
                    listTodo.getSelectionModel().clearSelection();
                    txtUpdateDelete.clear();
                } else {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR, "Deleted Failed", ButtonType.OK);
                    alert1.show();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtUpdateDelete.getText();

        ToDoTM selectedItem = listTodo.getSelectionModel().getSelectedItem();

        Connection conn = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("update todolist set description = ? where id = ?");
            preparedStatement.setObject(1, description);
            preparedStatement.setObject(2, selectedItem.getId());

            int i = preparedStatement.executeUpdate();
            if(i>0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Updated Successfully", ButtonType.OK);
                alert.show();
                loadList();
                btnUpdateDeleteDesable(true);
                listTodo.getSelectionModel().clearSelection();
                txtUpdateDelete.clear();


            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Updated Failed", ButtonType.OK);
                alert.show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
