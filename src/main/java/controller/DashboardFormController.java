package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalTime;
import static java.time.format.DateTimeFormatter.*;

public class DashboardFormController {
    public AnchorPane pane;
    public Label lblTime;

    public void initialize(){
        calculateTime();
    }

    private void calculateTime() {
     Timeline timeline =   new Timeline(new KeyFrame(
                Duration.ZERO,
                actionEvent -> lblTime.setText(LocalTime.now().format(ofPattern("HH:mm:ss")))
                ),new KeyFrame(Duration.seconds(1)));

     timeline.setCycleCount(Animation.INDEFINITE);
     timeline.play();
    }

    public void customerButtonOnAcction(ActionEvent actionEvent) {
        Stage stage = (Stage)pane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Customer Form");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void itemsButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage)pane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ItemForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Item Form");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeOrderButtonOnAcction(ActionEvent actionEvent) {
        Stage stage = (Stage)pane.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PlaceOrderForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Place Order Form");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
