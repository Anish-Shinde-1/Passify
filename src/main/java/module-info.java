module com.passify.passify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires java.sql;
    requires junit;
    requires mysql.connector.j;

    opens com.passify to javafx.fxml;
    opens com.passify.controller to javafx.fxml;
    opens com.passify.views;
    exports com.passify;
    exports com.passify.model;
    exports com.passify.controller;


}