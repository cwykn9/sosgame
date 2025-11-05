package cs449;

import cs449.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ToggleGroup modeGroup = new ToggleGroup();     // Simple / General
    private final ToggleGroup letterGroup = new ToggleGroup();   // S / O
    private final ComboBox<Integer> sizeBox = new ComboBox<>();  // board sizes
    private final Label status = new Label();
    private GridPane grid;

    private SosGame game;
    private Button[][] cells;

    @Override
    public void start(Stage stage) {
        // ---------- Top controls ----------
        sizeBox.getItems().addAll(3, 4, 5, 6, 7, 8, 9, 10);
        sizeBox.getSelectionModel().select(Integer.valueOf(3));

        // Mode radios with enum userData (avoid fragile .getText() checks)
        RadioButton simple = new RadioButton("Simple");
        RadioButton general = new RadioButton("General");
        simple.setToggleGroup(modeGroup);
        general.setToggleGroup(modeGroup);
        simple.setUserData(GameMode.SIMPLE);
        general.setUserData(GameMode.GENERAL);
        general.setSelected(true); // default if you want

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> startNewGame());

        // Letter radios
        RadioButton sBtn = new RadioButton("S");
        RadioButton oBtn = new RadioButton("O");
        sBtn.setToggleGroup(letterGroup);
        oBtn.setToggleGroup(letterGroup);
        sBtn.setSelected(true);

        HBox top = new HBox(12,
                new Label("Board:"), sizeBox,
                new Label("Mode:"), simple, general,
                newGameBtn,
                new Label("Letter:"), sBtn, oBtn
        );
        top.setPadding(new Insets(10));

        // ---------- Board Grid ----------
        grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(10));

        // ---------- Status line ----------
        status.setPadding(new Insets(10));

        // ---------- Layout ----------
        VBox root = new VBox(top, grid, status);
        Scene scene = new Scene(root);
        stage.setTitle("SOS — Sprint 3");
        stage.setScene(scene);
        stage.show();

        // ---------- Initialize game ----------
        startNewGame();
    }

    /** Create a new game and rebuild board */
    private void startNewGame() {
        int n = sizeBox.getValue() == null ? 3 : sizeBox.getValue();
        GameMode selectedMode = getSelectedMode();

        // Construct correct subclass based on enum (no text comparisons)
        game = (selectedMode == GameMode.GENERAL) ? new GeneralGame(n) : new SimpleGame(n);

        buildBoardUI(n);
        updateBoardFromModel();
        updateStatus();
    }

    /** Build the visual board grid */
    private void buildBoardUI(int n) {
        grid.getChildren().clear();
        cells = new Button[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Button b = new Button(" ");
                b.setMinSize(44, 44);
                final int rr = r, cc = c;

                b.setOnAction(e -> handleMove(rr, cc, b));
                cells[r][c] = b;
                grid.add(b, c, r);
            }
        }
    }

    /** Handle player move */
    private void handleMove(int r, int c, Button b) {
        if (game.isOver()) return;
        Letter L = getSelectedLetter();

        if (game.place(r, c, L)) {
            b.setText(L == Letter.S ? "S" : "O");
            updateStatus();
            if (game.isOver()) disableEmptyCells();
        }
    }

    // ---------- Helpers ----------

    private void updateBoardFromModel() {
        int n = game.board().size();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Letter L = game.board().get(r, c);
                cells[r][c].setText(letterToText(L));
                cells[r][c].setDisable(false);
            }
        }
    }

    private GameMode getSelectedMode() {
        Toggle t = modeGroup.getSelectedToggle();
        if (t != null && t.getUserData() instanceof GameMode gm) {
            return gm;
        }
        // Fallback: default to SIMPLE if somehow nothing selected
        return GameMode.SIMPLE;
    }

    private Letter getSelectedLetter() {
        Toggle t = letterGroup.getSelectedToggle();
        if (t instanceof RadioButton rb) {
            return "O".equalsIgnoreCase(rb.getText()) ? Letter.O : Letter.S;
        }
        return Letter.S;
    }

    private String letterToText(Letter L) {
        return switch (L) {
            case S -> "S";
            case O -> "O";
            default -> " ";
        };
    }

    private void disableEmptyCells() {
        int n = game.board().size();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (game.board().get(r, c) == Letter.EMPTY)
                    cells[r][c].setDisable(true);
            }
        }
    }

    /** Update status bar text */
    private void updateStatus() {
        String modeStr = "Mode: " + game.mode().name().toLowerCase();
        if (game.isOver()) {
            Player w = game.winner();
            String score = (game.mode() == GameMode.GENERAL)
                    ? String.format(" | Blue: %d  Red: %d", game.blueScore(), game.redScore())
                    : "";
            if (w == null) {
                status.setText("Game over: Draw  |  " + modeStr + score);
            } else {
                status.setText("Game over: Winner = " + w.name().toLowerCase() + "  |  " + modeStr + score);
            }
        } else {
            String score = (game.mode() == GameMode.GENERAL)
                    ? String.format(" | Blue: %d  Red: %d", game.blueScore(), game.redScore())
                    : "";
            status.setText("Current turn: " + game.turn().name().toLowerCase() + "  |  " + modeStr + score);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

