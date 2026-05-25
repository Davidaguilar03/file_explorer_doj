module uptc.edu.co.file_explorer_doj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens uptc.edu.co.file_explorer_doj to javafx.fxml;
    opens uptc.edu.co.file_explorer_doj.controller to javafx.fxml;

    exports uptc.edu.co.file_explorer_doj;
    exports uptc.edu.co.file_explorer_doj.controller;
    exports uptc.edu.co.file_explorer_doj.model;
    exports uptc.edu.co.file_explorer_doj.service;
}
