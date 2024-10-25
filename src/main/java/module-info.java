module se.iths.tictactoe {
    requires javafx.controls;
    requires javafx.fxml;

    opens se.iths.tictactoe to javafx.fxml;
    exports se.iths.tictactoe;
    exports se.iths.tictactoe.model;
    opens se.iths.tictactoe.model to javafx.fxml;
    exports se.iths.tictactoe.controller;
    opens se.iths.tictactoe.controller to javafx.fxml;
    exports se.iths.tictactoe.network;
    opens se.iths.tictactoe.network to javafx.fxml;
}
