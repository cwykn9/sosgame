package cs449;

import cs449.model.Board;
import cs449.model.GameMode;
import cs449.model.GeneralGame;
import cs449.model.Letter;
import cs449.model.Player;
import cs449.model.SimpleGame;
import cs449.model.SosGame;
import cs449.model.Participant;
import cs449.model.HumanParticipant;
import cs449.model.ComputerParticipant;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    /** What the user selects in the combo box (Human vs Computer). */
    private enum PlayerType {
        HUMAN, COMPUTER;

        @Override
        public String toString() {
            return this == HUMAN ? "Human" : "Computer";
        }
    }

    // ----- Model -----
    private SosGame game;
    private Participant blueParticipant;
    private Participant redParticipant;

    // ----- UI controls -----
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
    private Button recordButton;
    private Button replayButton;

    // ----- Recording / replay -----
    private boolean recording = false;
    private PrintWriter recordWriter;

    private boolean replaying = false;
    private List<Move> replayMoves;
    private int replayIndex = 0;

    @Override
    public void start(Stage stage) {
        // Board size selector
        boardSizeBox = new ComboBox<>();
        for (int n = 3; n <= 10; n++) {
            boardSizeBox.getItems().add(n);
        }
        boardSizeBox.setValue(3);
        Label boardLabel = new Label("Board:");
        boardLabel.setLabelFor(boardSizeBox);

        // Mode selector
        simpleModeBtn = new RadioButton("Simple");
        generalModeBtn = new RadioButton("General");
        ToggleGroup modeGroup = new ToggleGroup();
        simpleModeBtn.setToggleGroup(modeGroup);
        generalModeBtn.setToggleGroup(modeGroup);
        simpleModeBtn.setSelected(true);

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> {
            replaying = false;
            startNewGame();
        });

        HBox topRow = new HBox(10,
                boardLabel, boardSizeBox,
                new Label("Mode:"), simpleModeBtn, generalModeBtn,
                newGameBtn
        );
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));

        // Player type selectors
        blueTypeBox = new ComboBox<>();
        blueTypeBox.getItems().addAll(PlayerType.HUMAN, PlayerType.COMPUTER);
        blueTypeBox.setValue(PlayerType.HUMAN);

        redTypeBox = new ComboBox<>();
        redTypeBox.getItems().addAll(PlayerType.HUMAN, PlayerType.COMPUTER);
        redTypeBox.setValue(PlayerType.HUMAN);

        // Human letter choice
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

        // Board grid
        boardGrid = new GridPane();
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        boardGrid.setPadding(new Insets(10));

        // Status + Record/Replay buttons
        statusLabel = new Label();

        recordButton = new Button("Record Game");
        recordButton.setOnAction(e -> onRecordClicked());

        replayButton = new Button("Replay Game");
        replayButton.setOnAction(e -> onReplayClicked());

        HBox bottomRow = new HBox(10, statusLabel, recordButton, replayButton);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(10));

        VBox root = new VBox(5, topRow, secondRow, boardGrid, bottomRow);
        Scene scene = new Scene(root, 750, 520);

        stage.setTitle("SOS — Sprint 5");
        stage.setScene(scene);
        stage.show();

        startNewGame();
    }

    // ----- Game setup -----
    private void startNewGame() {
        int n = boardSizeBox.getValue();
        GameMode mode = simpleModeBtn.isSelected() ? GameMode.SIMPLE : GameMode.GENERAL;

        // Create the right kind of game
        if (mode == GameMode.SIMPLE) {
            game = new SimpleGame(n);
        } else {
            game = new GeneralGame(n);
        }
        game.newGame();

        // Build participants from combo box selections
        blueParticipant = (blueTypeBox.getValue() == PlayerType.HUMAN)
                ? new HumanParticipant(Player.BLUE)
                : new ComputerParticipant(Player.BLUE);

        redParticipant = (redTypeBox.getValue() == PlayerType.HUMAN)
                ? new HumanParticipant(Player.RED)
                : new ComputerParticipant(Player.RED);

        rebuildBoardGrid(n);
        updateBoardUI();
        updateStatusLabel();

        // Only auto-move computers in normal play, not during replay
        if (!replaying) {
            maybeScheduleComputerTurn();
        }
    }

    private void rebuildBoardGrid(int n) {
        boardGrid.getChildren().clear();
        cellButtons = new Button[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                Button btn = new Button();
                btn.setMinSize(50, 50);
                btn.setMaxSize(50, 50);
                final int rr = r;
                final int cc = c;
                btn.setOnAction(e -> handleCellClick(rr, cc));
                cellButtons[r][c] = btn;
                boardGrid.add(btn, c, r);
            }
        }
    }

    // ----- Human move -----
    private void handleCellClick(int r, int c) {
        if (game == null || game.isOver() || replaying) return;

        Participant current =
                (game.turn() == Player.BLUE) ? blueParticipant : redParticipant;

        // Ignore clicks when it's a computer's turn
        if (current.isComputer()) {
            return;
        }

        Letter chosen = letterSBtn.isSelected() ? Letter.S : Letter.O;

        boolean placed = game.place(r, c, chosen);
        if (!placed) {
            return; // invalid move
        }

        // Record human move if we're recording
        recordMove(current.color(), chosen, r, c);

        updateBoardUI();
        updateStatusLabel();

        // After human move, see if computer needs to play
        maybeScheduleComputerTurn();
    }

    // ----- UI updates -----
    private void updateBoardUI() {
        if (game == null) return;

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

        String modeStr = (game.mode() == GameMode.SIMPLE) ? "simple" : "general";
        String blueStr = blueParticipant.isComputer() ? "Computer" : "Human";
        String redStr = redParticipant.isComputer() ? "Computer" : "Human";

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
            // Stop recording at end of game
            if (recording) {
                stopRecording();
            }
        } else {
            String turnStr = (game.turn() == Player.BLUE) ? "blue" : "red";
            statusLabel.setText(
                    String.format("Current turn: %s  |  Mode: %s  |  Blue: %s  Red: %s",
                            turnStr, modeStr, blueStr, redStr));
        }
    }


    private void onRecordClicked() {
        if (replaying) return;

        if (!recording) {
            // Start recording
            try {
                recordWriter = new PrintWriter(new FileWriter("recording.txt", false));
                int n = boardSizeBox.getValue();
                String modeStr = simpleModeBtn.isSelected() ? "SIMPLE" : "GENERAL";
                recordWriter.printf("SIZE %d%n", n);
                recordWriter.printf("MODE %s%n", modeStr);
                recordWriter.flush();
                recording = true;
                recordButton.setText("Recording...");
                statusLabel.setText("Recording game to recording.txt");
            } catch (IOException ex) {
                recording = false;
                recordWriter = null;
                statusLabel.setText("Error: cannot start recording.");
            }
        } else {
            // Stop recording
            stopRecording();
        }
    }

    private void stopRecording() {
        if (recordWriter != null) {
            recordWriter.flush();
            recordWriter.close();
        }
        recordWriter = null;
        recording = false;
        recordButton.setText("Record Game");
    }

    private void recordMove(Player player, Letter letter, int row, int col) {
        if (!recording || recordWriter == null) return;
        String playerStr = (player == Player.BLUE) ? "BLUE" : "RED";
        String letterStr = (letter == Letter.S) ? "S" : "O";
        recordWriter.printf("MOVE %s %s %d %d%n", playerStr, letterStr, row, col);
        recordWriter.flush();
    }

    private void maybeScheduleComputerTurn() {
        if (game == null || game.isOver() || replaying) return;

        Participant current =
                (game.turn() == Player.BLUE) ? blueParticipant : redParticipant;

        if (!current.isComputer()) {
            return; // next player is human
        }

        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> {
            if (game.isOver() || replaying) {
                updateStatusLabel();
                return;
            }

            // Snapshot board BEFORE move
            Letter[][] before = copyBoard(game.board());

            // Computer makes move (inside model)
            current.makeMove(game);

            // Detect changed cell
            Board after = game.board();
            int n = after.size();
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (before[r][c] != after.get(r, c)) {
                        Letter L = after.get(r, c);
                        if (L == Letter.S || L == Letter.O) {
                            recordMove(current.color(), L, r, c);
                        }
                    }
                }
            }

            updateBoardUI();
            updateStatusLabel();

            maybeScheduleComputerTurn();
        });
        pause.play();
    }

    private Letter[][] copyBoard(Board b) {
        int n = b.size();
        Letter[][] arr = new Letter[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                arr[r][c] = b.get(r, c);
            }
        }
        return arr;
    }

    private void onReplayClicked() {
        // Stop any recording
        if (recording) {
            stopRecording();
        }
        if (replaying) return;

        File file = new File("recording.txt");
        if (!file.exists()) {
            statusLabel.setText("No recording.txt found to replay.");
            return;
        }

        int size = 3;
        GameMode mode = GameMode.SIMPLE;
        List<Move> moves = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts[0].equals("SIZE") && parts.length >= 2) {
                    size = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("MODE") && parts.length >= 2) {
                    mode = parts[1].equalsIgnoreCase("GENERAL")
                            ? GameMode.GENERAL
                            : GameMode.SIMPLE;
                } else if (parts[0].equals("MOVE") && parts.length >= 5) {
                    String letterStr = parts[2];
                    int row = Integer.parseInt(parts[3]);
                    int col = Integer.parseInt(parts[4]);
                    Letter L = letterStr.equalsIgnoreCase("O") ? Letter.O : Letter.S;
                    moves.add(new Move(L, row, col));
                }
            }
        } catch (IOException ex) {
            statusLabel.setText("Error reading recording.txt");
            return;
        }

        if (moves.isEmpty()) {
            statusLabel.setText("Recording has no moves.");
            return;
        }

        replayMoves = moves;
        replayIndex = 0;
        replaying = true;

        // Configure UI to match recording
        boardSizeBox.setValue(size);
        if (mode == GameMode.SIMPLE) {
            simpleModeBtn.setSelected(true);
        } else {
            generalModeBtn.setSelected(true);
        }

        // Start fresh game matching recording settings
        startNewGame();
        statusLabel.setText("Replaying recording.txt");
        playNextReplayMove();
    }

    private void playNextReplayMove() {
        if (!replaying || replayMoves == null) {
            replaying = false;
            return;
        }
        if (replayIndex >= replayMoves.size()) {
            replaying = false;
            updateStatusLabel();
            return;
        }

        Move m = replayMoves.get(replayIndex++);
        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> {
            if (game == null || game.isOver()) {
                replaying = false;
                updateStatusLabel();
                return;
            }
            game.place(m.row, m.col, m.letter);
            updateBoardUI();
            updateStatusLabel();
            playNextReplayMove();
        });
        pause.play();
    }

    // Simple record type for stored moves
    private static class Move {
        final Letter letter;
        final int row;
        final int col;

        Move(Letter letter, int row, int col) {
            this.letter = letter;
            this.row = row;
            this.col = col;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
