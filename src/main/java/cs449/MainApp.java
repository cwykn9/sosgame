package cs449;

import cs449.model.Board;
import cs449.model.GameMode;
import cs449.model.GeneralGame;
import cs449.model.Letter;
import cs449.model.Player;
import cs449.model.SimpleGame;
import cs449.model.SosGame;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainApp extends Application {

    private enum PlayerType {
        HUMAN, COMPUTER;

        @Override
        public String toString() {
            return this == HUMAN ? "Human" : "Computer";
        }
    }


    private SosGame game;

    private ComboBox<Integer> boardSizeBox;
    private RadioButton simpleModeBtn;
    private RadioButton generalModeBtn;

    private ComboBox<PlayerType> blueTypeBox;
    private ComboBox<PlayerType> redTypeBox;

    private RadioButton letterSBtn;
    private RadioButton letterOBtn;

    private GridPane boardGrid;
    private Button[][] cellButtons;

    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        boardSizeBox = new ComboBox<>();
        for (int n = 3; n <= 10; n++) {
            boardSizeBox.getItems().add(n);
        }
        boardSizeBox.setValue(3);

        Label boardLabel = new Label("Board:");
        boardLabel.setLabelFor(boardSizeBox);

        simpleModeBtn = new RadioButton("Simple");
        generalModeBtn = new RadioButton("General");
        ToggleGroup modeGroup = new ToggleGroup();
        simpleModeBtn.setToggleGroup(modeGroup);
        generalModeBtn.setToggleGroup(modeGroup);
        simpleModeBtn.setSelected(true);

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> startNewGame());

        HBox topRow = new HBox(10,
                boardLabel, boardSizeBox,
                new Label("Mode:"), simpleModeBtn, generalModeBtn,
                newGameBtn
        );
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));

        blueTypeBox = new ComboBox<>();
        blueTypeBox.getItems().addAll(PlayerType.HUMAN, PlayerType.COMPUTER);
        blueTypeBox.setValue(PlayerType.HUMAN);

        redTypeBox = new ComboBox<>();
        redTypeBox.getItems().addAll(PlayerType.HUMAN, PlayerType.COMPUTER);
        redTypeBox.setValue(PlayerType.HUMAN);

        letterSBtn = new RadioButton("S");
        letterOBtn = new RadioButton("O");
        ToggleGroup letterGroup = new ToggleGroup();
        letterSBtn.setToggleGroup(letterGroup);
        letterOBtn.setToggleGroup(letterGroup);
        letterSBtn.setSelected(true);

        HBox secondRow = new HBox(15,
                new Label("Blue Player:"), blueTypeBox,
                new Label("Red Player:"), redTypeBox,
                new Label("Letter (Human):"), letterSBtn, letterOBtn
        );
        secondRow.setAlignment(Pos.CENTER_LEFT);
        secondRow.setPadding(new Insets(0, 10, 10, 10));

        boardGrid = new GridPane();
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        boardGrid.setPadding(new Insets(10));

        statusLabel = new Label();
        HBox bottomRow = new HBox(statusLabel);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(10));

        VBox root = new VBox(5, topRow, secondRow, boardGrid, bottomRow);
        Scene scene = new Scene(root, 700, 500);

        stage.setTitle("SOS — Sprint 4");
        stage.setScene(scene);
        stage.show();

        startNewGame();
    }

    private void startNewGame() {
        int n = boardSizeBox.getValue();
        GameMode mode = simpleModeBtn.isSelected() ? GameMode.SIMPLE : GameMode.GENERAL;

        if (mode == GameMode.SIMPLE) {
            game = new SimpleGame(n);
        } else {
            game = new GeneralGame(n);
        }

        game.newGame();
        rebuildBoardGrid(n);
        updateBoardUI();
        updateStatusLabel();

        maybeScheduleComputerTurn();
    }

    private void rebuildBoardGrid(int n) {
        boardGrid.getChildren().clear();
        cellButtons = new Button[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Button btn = new Button();
                btn.setMinSize(50, 50);
                btn.setMaxSize(50, 50);
                int rr = r;
                int cc = c;
                btn.setOnAction(e -> handleCellClick(rr, cc));
                cellButtons[r][c] = btn;
                boardGrid.add(btn, c, r);
            }
        }
    }

    private void handleCellClick(int r, int c) {
        if (game == null || game.isOver()) return;

        Player currentTurn = game.turn();
        PlayerType type =
                (currentTurn == Player.BLUE) ? blueTypeBox.getValue() : redTypeBox.getValue();

        if (type == PlayerType.COMPUTER) {
            return;
        }

        Letter chosen = letterSBtn.isSelected() ? Letter.S : Letter.O;

        boolean placed = game.place(r, c, chosen);
        if (!placed) {
            return;
        }

        updateBoardUI();
        updateStatusLabel();

        maybeScheduleComputerTurn();
    }

    private void updateBoardUI() {
        Board b = game.board();
        int n = b.size();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Letter L = b.get(r, c);
                String text = "";
                if (L == Letter.S) text = "S";
                else if (L == Letter.O) text = "O";
                cellButtons[r][c].setText(text);
            }
        }
    }

    private void updateStatusLabel() {
        if (game == null) {
            statusLabel.setText("");
            return;
        }

        String modeStr = game.mode() == GameMode.SIMPLE ? "simple" : "general";
        String blueStr = blueTypeBox.getValue().toString();
        String redStr = redTypeBox.getValue().toString();

        if (game.isOver()) {
            Player w = game.winner();
            if (w == null) {
                statusLabel.setText(
                        String.format("Game over: draw | Mode: %s | Blue: %s  Red: %s",
                                modeStr, blueStr, redStr));
            } else {
                statusLabel.setText(
                        String.format("Game over: %s wins | Mode: %s | Blue: %s  Red: %s",
                                (w == Player.BLUE ? "blue" : "red"),
                                modeStr, blueStr, redStr));
            }
        } else {
            String turnStr = game.turn() == Player.BLUE ? "blue" : "red";
            statusLabel.setText(
                    String.format("Current turn: %s  |  Mode: %s  |  Blue: %s  Red: %s",
                            turnStr, modeStr, blueStr, redStr));
        }
    }


    private void maybeScheduleComputerTurn() {
        if (game == null || game.isOver()) return;

        Player turn = game.turn();
        PlayerType type =
                (turn == Player.BLUE) ? blueTypeBox.getValue() : redTypeBox.getValue();

        if (type != PlayerType.COMPUTER) {
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> {
            if (game.isOver()) {
                updateStatusLabel();
                return;
            }

            makeComputerMove();
            updateBoardUI();
            updateStatusLabel();


            maybeScheduleComputerTurn();
        });
        pause.play();
    }

    private void makeComputerMove() {
        if (game == null || game.isOver()) return;

        Board b = game.board();
        int n = b.size();

        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (b.isEmpty(r, c)) {
                    empties.add(new int[]{r, c});
                }
            }
        }
        if (empties.isEmpty()) return;

        int[] rc = empties.get(ThreadLocalRandom.current().nextInt(empties.size()));
        int r = rc[0], c = rc[1];

        Player current = game.turn();
        PlayerType blueType = blueTypeBox.getValue();
        PlayerType redType  = redTypeBox.getValue();

        boolean blueIsHuman = (blueType == PlayerType.HUMAN);
        boolean redIsHuman  = (redType == PlayerType.HUMAN);

        Letter L;

        if (current == Player.BLUE && blueType == PlayerType.COMPUTER && redIsHuman) {
            L = letterSBtn.isSelected() ? Letter.O : Letter.S;
        } else if (current == Player.RED && redType == PlayerType.COMPUTER && blueIsHuman) {
            L = letterSBtn.isSelected() ? Letter.O : Letter.S;
        } else {
            L = ThreadLocalRandom.current().nextBoolean() ? Letter.S : Letter.O;
        }

        game.place(r, c, L);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
