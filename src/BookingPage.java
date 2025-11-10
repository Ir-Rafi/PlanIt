import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingPage {

    // --- Database connection ---
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
        String user = "ununqd8usvy0wouy";
        String password = "GmDEehgTBjzyuPRuA8i8";
        return DriverManager.getConnection(url, user, password);
    }

    // --- Main BookingPage UI ---
    public BookingPage(Stage stage, Scene previousScene, int organizerId, int eventId) {
        VBox layout = new VBox(40);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1f1c2c, #928dab);"); // Dark gradient

        // Title
        Label title = new Label("Choose a Place to Book");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Place cards container
        HBox cardContainer = new HBox(30);
        cardContainer.setAlignment(Pos.CENTER);

        // Create cards with images
        VBox tscCard = createPlaceCard("TSC", "img/tsc.jpeg", stage, organizerId, previousScene);
        VBox curzonCard = createPlaceCard("Curzon Hall", "img/curzon.jpg", stage, organizerId, previousScene);
        VBox senateCard = createPlaceCard("Senate", "img/senate.jpg", stage, organizerId, previousScene);

        cardContainer.getChildren().addAll(tscCard, curzonCard, senateCard);

        // Back button
        Button back = new Button("⬅ Back");
        back.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;");
        back.setOnMouseEntered(e -> back.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;"));
        back.setOnMouseExited(e -> back.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;"));
        back.setOnAction(e -> stage.setScene(previousScene));

        layout.getChildren().addAll(title, cardContainer, back);

        stage.setScene(new Scene(layout, 950, 650));
    }

    // --- Create each place card ---
    private VBox createPlaceCard(String placeName, String imagePath, Stage stage, int organizerId, Scene previousScene) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10,0,0,5);");
        card.setPrefWidth(220);

        // Place image
        ImageView imageView;
        try {
            Image image = new Image(imagePath, 200, 150, true, true);
            imageView = new ImageView(image);
        } catch (Exception e) {
            imageView = new ImageView();
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
        }

        // Place label
        Label label = new Label(placeName);
        label.setStyle("-fx-font-size: 20px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        // Book button
        Button selectBtn = new Button("Book Now");
        selectBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 8 20;");
        selectBtn.setOnMouseEntered(e -> selectBtn.setStyle("-fx-background-color: #1B4F72; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 8 20;"));
        selectBtn.setOnMouseExited(e -> selectBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 8 20;"));
        selectBtn.setOnAction(e -> showAvailability(stage, placeName, organizerId, previousScene));

        card.getChildren().addAll(imageView, label, selectBtn);
        return card;
    }

    // --- Availability and Booking UI ---
    private void showAvailability(Stage stage, String placeName, int organizerId, Scene previousScene) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1f1c2c, #928dab);");

        Label title = new Label("Check Availability: " + placeName);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Inputs
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");
        TextField endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");

        // Styles for inputs
        String inputStyle = """
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #2E86DE;
            -fx-border-width: 2;
            -fx-background-color: rgba(255,255,255,0.1);
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 8 12;
        """;
        startDatePicker.setStyle(inputStyle);
        endDatePicker.setStyle(inputStyle);
        startTimeField.setStyle(inputStyle);
        endTimeField.setStyle(inputStyle);

        // Labels
        Label startLabel = new Label("Start Date:");
        Label endLabel = new Label("End Date:");
        Label startTimeLabel = new Label("Start Time (HH:mm):");
        Label endTimeLabel = new Label("End Time (HH:mm):");
        startLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        endLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        startTimeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        endTimeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Check availability button
        Button checkBtn = new Button("Check Availability");
        checkBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;");
        checkBtn.setOnMouseEntered(e -> checkBtn.setStyle("-fx-background-color: #1E8449; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;"));
        checkBtn.setOnMouseExited(e -> checkBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;"));

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button back = new Button("⬅ Back");
        back.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;");
        back.setOnMouseEntered(e -> back.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;"));
        back.setOnMouseExited(e -> back.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"));
        back.setOnAction(e -> stage.setScene(previousScene));

        // Booking card container
        VBox bookingCard = new VBox(15);
        bookingCard.setPadding(new Insets(20));
        bookingCard.setAlignment(Pos.CENTER);
        bookingCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10,0,0,5);");

        bookingCard.getChildren().addAll(
                title,
                startLabel, startDatePicker,
                endLabel, endDatePicker,
                startTimeLabel, startTimeField,
                endTimeLabel, endTimeField,
                checkBtn, resultLabel, back
        );

        layout.getChildren().add(bookingCard);

        // Check availability logic
        checkBtn.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            try {
                LocalTime startTime = LocalTime.parse(startTimeField.getText());
                LocalTime endTime = LocalTime.parse(endTimeField.getText());

                try (Connection conn = connect()) {
                    PreparedStatement ps = conn.prepareStatement("""
                        SELECT COUNT(*) FROM bookings
                        WHERE place_name = ?
                          AND (
                                (start_date <= ? AND end_date >= ?)
                            AND (start_time < ? AND end_time > ?)
                          )
                    """);
                    ps.setString(1, placeName);
                    ps.setDate(2, Date.valueOf(endDate));
                    ps.setDate(3, Date.valueOf(startDate));
                    ps.setTime(4, Time.valueOf(endTime));
                    ps.setTime(5, Time.valueOf(startTime));

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    boolean isFree = rs.getInt(1) == 0;

                    if (isFree) {
                        resultLabel.setText("✅ Place available! Booking confirmed.");
                        bookPlace(conn, placeName, organizerId, startDate, endDate, startTime, endTime);
                    } else {
                        resultLabel.setText("❌ Already booked for that time slot.");
                    }
                }
            } catch (Exception ex) {
                resultLabel.setText("Error: " + ex.getMessage());
            }
        });
        double width = previousScene.getWidth();
        double height = previousScene.getHeight();
        stage.setScene(new Scene(layout, width, height));
    }

    // --- Book the place in DB ---
    private void bookPlace(Connection conn, String placeName, int organizerId,
                           LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) throws SQLException {
        PreparedStatement insert = conn.prepareStatement("""
            INSERT INTO bookings (place_name, organizer_id, start_date, end_date, start_time, end_time)
            VALUES (?, ?, ?, ?, ?, ?)
        """);
        insert.setString(1, placeName);
        insert.setInt(2, organizerId);
        insert.setDate(3, Date.valueOf(startDate));
        insert.setDate(4, Date.valueOf(endDate));
        insert.setTime(5, Time.valueOf(startTime));
        insert.setTime(6, Time.valueOf(endTime));
        insert.executeUpdate();
    }
}
