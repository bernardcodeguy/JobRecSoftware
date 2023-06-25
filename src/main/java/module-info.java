module jobs.rec.jobrecsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens jobs.rec.jobrecsoftware to javafx.fxml;
    exports jobs.rec.jobrecsoftware;
}