package cs449;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label title = new Label("Hello, SOS!");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        Canvas canvas = new Canvas(360, 200);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setLineWidth(2);
        g.strokeLine(40, 40, 320, 180);
        g.strokeLine(40, 180, 320, 40);


        CheckBox enable = new CheckBox("Check Box");
        RadioButton r1 = new RadioButton("Option 1");
        RadioButton r2 = new RadioButton("Option 2");
        ToggleGroup tg = new ToggleGroup();
        r1.setToggleGroup(tg); r2.setToggleGroup(tg);
        r1.setDisable(true); r2.setDisable(true);
        enable.selectedProperty().addListener((obs, was, on) -> {
            r1.setDisable(!on); r2.setDisable(!on);
        });

        HBox controls = new HBox(12, enable, r1, r2);
        controls.setPadding(new Insets(8));

        VBox root = new VBox(12, title, canvas, controls);
        root.setPadding(new Insets(12));

        stage.setTitle("Sprint 0 Test");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
