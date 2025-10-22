package cs449;

import cs449.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    private SosGame game;
    private Button[][] cells;

    private final ToggleGroup letterGroup = new ToggleGroup(); // S / O
    private final ToggleGroup modeGroup   = new ToggleGroup(); // Simple / General
    private final ComboBox<Integer> sizeBox = new ComboBox<>(); // 3..10
    private final Label status = new Label();

    @Override
    public void start(Stage stage) {
        // --- Controls (top bar) ---
        sizeBox.getItems().addAll(3,4,5,6,7,8,9,10);
        sizeBox.getSelectionModel().select(Integer.valueOf(3));

        RadioButton simple  = new RadioButton("Simple");
        RadioButton general = new RadioButton("General");
        simple.setToggleGroup(modeGroup);
        general.setToggleGroup(modeGroup);
        simple.setSelected(true); // default

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> startNewGame());

        RadioButton sBtn = new RadioButton("S");
        RadioButton oBtn = new RadioButton("O");
        sBtn.setToggleGroup(letterGroup);
        oBtn.setToggleGroup(letterGroup);
        sBtn.setSelected(true); // default

        HBox top = new HBox(
                12,
                new Label("Board:"), sizeBox,
                new Label("Mode:"), simple, general,
                newGameBtn,
                new Label("Letter:"), sBtn, oBtn
        );
        top.setPadding(new Insets(10));

        // --- Board area (center) ---
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setPadding(new Insets(10));

        // --- Status (bottom) ---
        status.setPadding(new Insets(10));

        VBox root = new VBox(top, grid, status);

        stage.setTitle("SOS — Sprint 2");
        stage.setScene(new Scene(root));
        stage.show();

        // First game
        startNewGame();
    }

    // Starts (or restarts) a game using UI selections
    private void startNewGame() {
        Integer val = sizeBox.getValue();
        int n = (val == null ? 3 : val);

        // Safe read of mode toggle
        Toggle t = modeGroup.getSelectedToggle();
        GameMode mode = (t instanceof RadioButton rb && "General".equalsIgnoreCase(rb.getText()))
                ? GameMode.GENERAL : GameMode.SIMPLE;

        if (game == null) game = new SosGame(n, mode);
        else game.newGame(n, mode);

        buildBoardUI(n);
        updateStatus();
    }

    // Rebuild the board buttons and wire them to game.place(...)
    private void buildBoardUI(int n) {
        GridPane grid = (GridPane) ((VBox) status.getParent()).getChildren().get(1);
        grid.getChildren().clear();

        cells = new Button[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Button b = new Button(" ");
                b.setMinSize(40, 40);

                final int rr = r, cc = c;
                b.setOnAction(e -> {
                    // Safe read of letter toggle
                    Toggle t = letterGroup.getSelectedToggle();
                    Letter L = (t instanceof RadioButton rb && "S".equals(rb.getText()))
                            ? Letter.S : Letter.O;

                    if (game.place(rr, cc, L)) {
                        b.setText(L == Letter.S ? "S" : "O");
                        updateStatus();
                    }
                });

                cells[r][c] = b;
                grid.add(b, c, r);
            }
        }
    }

    private void updateStatus() {
        status.setText("Current turn: " + game.turn().name().toLowerCase());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

