package com.example;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

import java.util.Optional;
import java.io.File;

public class TheaterFX extends Application {
    private static final int ROWS = 6;
    private static final int COLS = 10;
    private static final int AISLE_AFTER_COL = 5; 

    private SeatManager manager;
    private Button[][] seatBtns;
    private Label status;

    @Override
    public void start(Stage stage) {
        manager = new SeatManager(ROWS, COLS);
        seatBtns = new Button[ROWS][COLS];

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        
        VBox top = new VBox(10);
        top.setFillWidth(true);

        Label screen = new Label("SCREEN");
        screen.setFont(Font.font(18));
        screen.setStyle("""
            -fx-background-color: linear-gradient(to bottom,#cbd5e1,#94a3b8);
            -fx-padding:10 20;
            -fx-background-radius:10;
            -fx-font-weight:bold;
        """);

        StackPane screenBar = new StackPane(screen);
        screenBar.setAlignment(Pos.CENTER);
        screenBar.setPadding(new Insets(0, 0, 0, 0));
        screenBar.setMaxWidth(Double.MAX_VALUE);

        ToolBar tb = new ToolBar();
        Button suggest = new Button("Suggest Seat");
        Button best = new Button("Best Seat");
        Button find2 = new Button("Find 2 Together");
        Button find4 = new Button("Find 4 Together");
        Button reserve2 = new Button("Reserve 2");
        Button reserve4 = new Button("Reserve 4");
        Button snapshot = new Button("Save Screenshot");
        Button reset = new Button("Reset");

        suggest.setOnAction(e -> suggestSeat());
        best.setOnAction(e -> suggestBestSeat());
        find2.setOnAction(e -> suggestAdjacent(2));
        find4.setOnAction(e -> suggestAdjacent(4));
        reserve2.setOnAction(e -> reserveAdjacent(2));
        reserve4.setOnAction(e -> reserveAdjacent(4));
        snapshot.setOnAction(e -> saveScreenshot(stage));
        reset.setOnAction(e -> { manager = new SeatManager(ROWS, COLS); refresh(); setStatus("Theater reset."); });

        tb.getItems().addAll(
                suggest, best,
                new Separator(),
                find2, find4,
                new Separator(),
                reserve2, reserve4,
                new Separator(),
                snapshot,
                new Separator(),
                reset
        );

        top.getChildren().addAll(screenBar, tb);
        root.setTop(top);

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8); grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);

        
        for (int c = 0; c < COLS; c++) {
            Label lbl = new Label(String.valueOf(c + 1));
            GridPane.setHalignment(lbl, HPos.CENTER);
            grid.add(lbl, mapCol(c), 0);
        }

       
        for (int r = 0; r < ROWS; r++) {
            Label rowLabel = new Label(String.valueOf((char)('A' + r)));
            grid.add(rowLabel, 0, r + 1);
            GridPane.setHalignment(rowLabel, HPos.CENTER);

            for (int c = 0; c < COLS; c++) {
                Button b = new Button(label(r, c));
                b.setPrefSize(56, 34);
                b.setTooltip(new Tooltip(label(r,c)));
                styleOpen(b);
                final int rr = r, cc = c;
                b.setOnAction(e -> handleSeatClick(rr, cc));
                seatBtns[r][c] = b;
                grid.add(b, mapCol(c), r + 1);
            }
        }

        
        grid.getColumnConstraints().add(new ColumnConstraints(28)); 
        for (int ui = 1; ui <= COLS + 1; ui++) { 
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHalignment(HPos.CENTER);
            cc.setMinWidth(ui == AISLE_AFTER_COL + 1 ? 22 : 60);
            grid.getColumnConstraints().add(cc);
        }
        root.setCenter(grid);

        
        HBox bottom = new HBox(16);
        Label legend = new Label("Legend:  Open = 🟩  Reserved = 🟥");
        status = new Label("Click a green seat to reserve. Click a red seat to cancel.");
        HBox.setHgrow(status, Priority.ALWAYS);
        bottom.getChildren().addAll(legend, status);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(0, 10, 10, 10));
        root.setBottom(bottom);

        Scene scene = new Scene(root, 960, 600);
        scene.setFill(javafx.scene.paint.Paint.valueOf("#0f172a"));
        root.setStyle("-fx-background-color: linear-gradient(to bottom,#0f172a,#111827); -fx-text-fill:#e5e7eb;");

        stage.setTitle("Movie Theater Seat Reservation (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    private int mapCol(int c) { 
        int ui = c + 1;
        if (c >= AISLE_AFTER_COL) ui += 1;
        return ui;
    }

    private String label(int r, int c) { return "" + (char)('A' + r) + (c + 1); }

    private void handleSeatClick(int r, int c) {
        if (manager.isAvailable(r, c)) {
            manager.reserve(r, c);
            setStatus("Booked " + label(r,c) + " ✅");
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Cancel reservation for " + label(r,c) + "?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText(null);
            confirm.setTitle("Cancel reservation");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                manager.cancel(r, c);
                setStatus("Cancelled " + label(r,c) + " ↩");
            }
        }
        refresh();
    }

    private void refresh() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Button b = seatBtns[r][c];
                if (manager.isAvailable(r,c)) styleOpen(b); else styleReserved(b);
            }
        }
    }

    
    private void suggestSeat() {
        manager.suggestFirstAvailable().ifPresentOrElse(rc -> {
            setStatus("Suggested: " + label(rc[0], rc[1]));
            Button b = seatBtns[rc[0]][rc[1]];
            b.requestFocus();
            pulse(b);
        }, () -> setStatus("No seats available."));
    }

    private void suggestBestSeat() {
        double cr = (ROWS - 1) / 2.0;
        double cc = (COLS - 1) / 2.0;

        int bestR = -1, bestC = -1;
        double bestD = Double.POSITIVE_INFINITY;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (manager.isAvailable(r, c)) {
                    
                    double dr = (r - cr) * 1.1;
                    double dc = (c - cc);
                    double d = dr * dr + dc * dc;
                    if (d < bestD) { bestD = d; bestR = r; bestC = c; }
                }
            }
        }

        if (bestR >= 0) {
            setStatus("Best seat: " + label(bestR, bestC));
            Button b = seatBtns[bestR][bestC];
            b.requestFocus();
            pulse(b);
        } else {
            setStatus("No seats available.");
        }
    }

    private void suggestAdjacent(int k) {
        if (k < 2) { setStatus("Group size must be ≥ 2"); return; }
        for (int r = 0; r < ROWS; r++) {
            int run = 0, start = 0;
            for (int c = 0; c < COLS; c++) {
                if (manager.isAvailable(r, c)) {
                    if (run == 0) start = c;
                    run++;
                    if (run >= k) {
                        for (int x = start; x < start + k; x++) pulse(seatBtns[r][x]);
                        setStatus("Suggested " + k + " adjacent: " + (char)('A'+r) + (start+1) + "–" + (start+k));
                        return;
                    }
                } else run = 0;
            }
        }
        setStatus("Could not find " + k + " adjacent open seats.");
    }

  
    private void reserveAdjacent(int k) {
        if (k < 2) { setStatus("Group size must be ≥ 2"); return; }
        for (int r = 0; r < ROWS; r++) {
            int run = 0, start = 0;
            for (int c = 0; c < COLS; c++) {
                if (manager.isAvailable(r, c)) {
                    if (run == 0) start = c;
                    run++;
                    if (run >= k) {
                        for (int x = start; x < start + k; x++) manager.reserve(r, x);
                        refresh();
                        setStatus("Booked " + k + " seats: " + (char)('A'+r) + (start+1) + "–" + (start+k) + " ✅");
                        
                        pulse(seatBtns[r][start]);
                        pulse(seatBtns[r][start + k - 1]);
                        return;
                    }
                } else run = 0;
            }
        }
        setStatus("No " + k + "-seat block available to book.");
    }

    
    private void saveScreenshot(Stage stage) {
        try {
            var img = stage.getScene().snapshot(null);
            File out = new File("screenshot-" + System.currentTimeMillis() + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", out);
            setStatus("Saved " + out.getName());
        } catch (Exception ex) {
            setStatus("Failed to save screenshot: " + ex.getMessage());
        }
    }

    private void setStatus(String s) { status.setText(s); }

    private void styleOpen(Button b) {
        b.setStyle("-fx-background-color:#34d399; -fx-text-fill:black; -fx-font-weight:bold; -fx-background-radius:8;");
    }
    private void styleReserved(Button b) {
        b.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8;");
    }

    private void pulse(Button b) {
        String add = "; -fx-border-color: black; -fx-border-width: 3;";
        b.setStyle(b.getStyle() + add);
        PauseTransition pt = new PauseTransition(Duration.millis(550));
        pt.setOnFinished(ev -> b.setStyle(b.getStyle()
                .replaceAll(";?\\s*-fx-border-color:\\s*black;?", "")
                .replaceAll(";?\\s*-fx-border-width:\\s*3;?", "")));
        pt.play();
    }

    public static void main(String[] args) { launch(args); }
}
