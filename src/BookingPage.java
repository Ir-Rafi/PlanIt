import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public BookingPage(Stage stage, Scene previousScene, int eventId) {
        int organizerId = Session.getOrganizerId();
        VBox layout = new VBox(40);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1f1c2c, #928dab);");

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

        stage.setScene(new Scene(layout, 1920, 1080));
         stage.setFullScreen(true);
stage.setFullScreenExitHint("");
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

        // ComboBoxes for time selection (30-min interval)
        ComboBox<String> startTimeCombo = new ComboBox<>();
        ComboBox<String> endTimeCombo = new ComboBox<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
        startTimeCombo.setPromptText("Start Time");
        endTimeCombo.setPromptText("End Time");
        startTimeCombo.setValue("00:00");
        endTimeCombo.setValue("00:00");

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
        startTimeCombo.setStyle(inputStyle);
        endTimeCombo.setStyle(inputStyle);

        // Labels
        Label startLabel = new Label("Start Date:");
        Label endLabel = new Label("End Date:");
        Label startTimeLabel = new Label("Start Time:");
        Label endTimeLabel = new Label("End Time:");
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
                startTimeLabel, startTimeCombo,
                endTimeLabel, endTimeCombo,
                checkBtn, resultLabel, back
        );

        layout.getChildren().add(bookingCard);

        // Check availability logic with animated "Checking" effect
        checkBtn.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String startTime = startTimeCombo.getValue();
            String endTime = endTimeCombo.getValue();

            if (startDate == null || endDate == null) {
                resultLabel.setText("❌ Please select start and end dates.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                resultLabel.setText("❌ End date cannot be before start date.");
                return;
            }
            if (startTime == null || endTime == null) {
                resultLabel.setText("❌ Please select start and end times.");
                return;
            }
            if (startDate.isEqual(endDate) && !isValidTimeRange(startTime, endTime)) {
                resultLabel.setText("❌ End time must be after start time on the same day.");
                return;
            }

            // Disable button during checking
            checkBtn.setDisable(true);
            
            // Create animated "Checking" effect using Timeline
            final String[] dots = {".", "..", "..."};
            final int[] dotIndex = {0};
            
            Timeline checkingAnimation = new Timeline(
                new KeyFrame(Duration.millis(500), event -> {
                    resultLabel.setText("Checking" + dots[dotIndex[0]]);
                    resultLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 18px; -fx-font-weight: bold;");
                    dotIndex[0] = (dotIndex[0] + 1) % dots.length;
                })
            );
            checkingAnimation.setCycleCount(Timeline.INDEFINITE);
            checkingAnimation.play();

            // Run database check in a separate thread
            new Thread(() -> {
                try {
                    // Simulate processing time (optional - remove if you want instant check)
                    Thread.sleep(1500);
                    
                    Connection conn = connect();
                    // Check for overlapping bookings
                    // Two bookings overlap if: NOT (booking1 ends before booking2 starts OR booking1 starts after booking2 ends)
                    PreparedStatement ps = conn.prepareStatement("""
                        SELECT COUNT(*) FROM bookings
                        WHERE place_name = ?
                          AND NOT (
                                (end_date < ? OR (end_date = ? AND end_time <= ?))
                                OR (start_date > ? OR (start_date = ? AND start_time >= ?))
                          )
                    """);
                    ps.setString(1, placeName);
                    // Booking ends before requested start
                    ps.setDate(2, Date.valueOf(startDate));
                    ps.setDate(3, Date.valueOf(startDate));
                    ps.setTime(4, Time.valueOf(startTime + ":00"));
                    // Booking starts after requested end
                    ps.setDate(5, Date.valueOf(endDate));
                    ps.setDate(6, Date.valueOf(endDate));
                    ps.setTime(7, Time.valueOf(endTime + ":00"));

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    boolean isFree = rs.getInt(1) == 0;

                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        checkingAnimation.stop();
                        if (isFree) {
                            resultLabel.setText("✅ Place available! Booking confirmed.");
                            resultLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-size: 18px; -fx-font-weight: bold;");
                            try {
                                bookPlace(conn, placeName, organizerId, startDate, endDate, startTime, endTime);
                            } catch (SQLException sqlEx) {
                                resultLabel.setText("Error booking: " + sqlEx.getMessage());
                                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px;");
                            }
                        } else {
                            resultLabel.setText("❌ Already booked for that time slot.");
                            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");
                        }
                        checkBtn.setDisable(false);
                    });
                    
                    conn.close();
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        checkingAnimation.stop();
                        resultLabel.setText("Error: " + ex.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px;");
                        checkBtn.setDisable(false);
                    });
                }
            }).start();
        });

        stage.setScene(new Scene(layout, previousScene.getWidth(), previousScene.getHeight()));
    }

    private boolean isValidTimeRange(String startTime, String endTime) {
        String[] startParts = startTime.split(":");
        String[] endParts = endTime.split(":");
        int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
        int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
        return endMinutes > startMinutes;
    }

    // --- Book the place in DB ---
    private void bookPlace(Connection conn, String placeName, int organizerId,
                           LocalDate startDate, LocalDate endDate, String startTime, String endTime) throws SQLException {
        PreparedStatement insert = conn.prepareStatement("""
            INSERT INTO bookings (place_name, organizer_id, start_date, end_date, start_time, end_time)
            VALUES (?, ?, ?, ?, ?, ?)
        """);
        insert.setString(1, placeName);
        insert.setInt(2, organizerId);
        insert.setDate(3, Date.valueOf(startDate));
        insert.setDate(4, Date.valueOf(endDate));
        insert.setTime(5, Time.valueOf(startTime + ":00"));
        insert.setTime(6, Time.valueOf(endTime + ":00"));
        insert.executeUpdate();
    }
}
