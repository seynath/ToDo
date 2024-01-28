module com.seynat.todo {
    requires javafx.controls;
    requires javafx.fxml;
    //requires mysql.connector.j;
    requires java.sql;


    opens com.seynat.todo to javafx.fxml;
    exports com.seynat.todo;
}