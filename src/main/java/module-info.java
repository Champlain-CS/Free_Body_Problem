module com.example.free_body_problem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.free_body_problem to javafx.fxml;
    exports com.example.free_body_problem;
    exports com.example.free_body_problem.old_Files;
    opens com.example.free_body_problem.old_Files to javafx.fxml;
}