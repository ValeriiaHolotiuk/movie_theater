import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Optional;

public class TheaterFX extends Application {
    private static final int ROWS = 6;
    private static final int COLS = 10;
    private static final int AISLE_AFTER_COL = 4; 

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
        Label screen = new Label("SCREEN");
        screen.setFont(Font.font(18));
        screen.setAlignment(Pos.CENTER);
        screen.setStyle("-fx-background-color: linear-gradient(to bottom,#cbd5e1,#94a3b8);"
                + "-fx-padding:10 20; -fx-background-radius:8; -fx-font-weight:bold;");

        ToolBar tb = new ToolBar();
        Button suggest = new Button("Suggest Seat");
        Button reset = new Button("Reset");
        suggest.setOnAction(e -> suggestSeat());
        reset.setOnAction(e -> { manager = new SeatManager(ROWS, COLS); refresh(); setStatus("Theater reset."); });
        tb.getItems().addAll(suggest, new Separator(), reset);

        top.getChildren().addAll(screen, tb);
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
            setStatus("Reserved " + label(r,c));
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel reservation for " + label(r,c) + "?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText(null);
            confirm.setTitle("Cancel reservation");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                manager.cancel(r, c);
                setStatus("Cancelled " + label(r,c));
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
            seatBtns[rc[0]][rc[1]].requestFocus();
            seatBtns[rc[0]][rc[1]].setStyle(seatBtns[rc[0]][rc[1]].getStyle() + "; -fx-border-color: black; -fx-border-width: 3;");
        }, () -> setStatus("No seats available."));
    }

    private void setStatus(String s) { status.setText(s); }

    private void styleOpen(Button b) {
        b.setStyle("-fx-background-color:#34d399; -fx-text-fill:black; -fx-font-weight:bold; -fx-background-radius:8;");
    }
    private void styleReserved(Button b) {
        b.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8;");
    }

    public static void main(String[] args) { launch(args); }
}
