import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class after_login {

    private Stage stage;
    private Scene dashboardScene;

    // Purano method - onno class gulo eita use kore
    public void applyHoverEffect(Button btn, String normalColor, String hoverColor) {
        btn.setStyle("-fx-background-color: " + normalColor + "; -fx-text-fill: white; -fx-background-radius: 10;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-background-radius: 10;");
            btn.setCursor(javafx.scene.Cursor.HAND);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: " + normalColor + "; -fx-text-fill: white; -fx-background-radius: 10;");
        });
    }

    // Notun modern hover effect
    public void applyModernHoverEffect(Button btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            
            Glow glow = new Glow(0.8);
            btn.setEffect(glow);
            btn.setCursor(javafx.scene.Cursor.HAND);
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            
            btn.setEffect(null);
        });
    }

    public void openEventPortal(Stage stage, Scene dashboardScene) {
        this.stage = stage;
        this.dashboardScene = dashboardScene;
        showHomeScreen();
    }

    // ---------------- HOME SCREEN ----------------
    private void showHomeScreen() {
        StackPane root = new StackPane();
        
        // Dark gradient background
        LinearGradient bgGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0f0c29")),
            new Stop(0.5, Color.web("#302b63")),
            new Stop(1, Color.web("#24243e"))
        );
        BackgroundFill bgFill = new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY);
        root.setBackground(new Background(bgFill));

        VBox homeLayout = new VBox(30);
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setPadding(new Insets(50));
        //homeLayout.setMaxWidth(700);

        // Back button at top left
        Button dashboardBack = new Button("← Back to Dashboard");
        dashboardBack.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        dashboardBack.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-radius: 8;"
        );
        applyModernHoverEffect(dashboardBack);
        dashboardBack.setOnAction(e -> {
            stage.setScene(dashboardScene);
            //stage.setMaximized(true);
            stage.setFullScreen(true);
stage.setFullScreenExitHint("");

        });

        HBox backContainer = new HBox(dashboardBack);
        backContainer.setAlignment(Pos.TOP_LEFT);

        // Glass card container
        VBox cardBox = new VBox(25);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(50, 60, 50, 60));
        cardBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05); " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-radius: 20; " +
            "-fx-border-width: 1;"
        );
        
        // Drop shadow effect for card
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(138, 98, 255, 0.4));
        cardShadow.setRadius(30);
        cardShadow.setSpread(0.3);
        cardBox.setEffect(cardShadow);

        // Calendar icon
        Circle iconCircle = new Circle(40);
        LinearGradient iconGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#a855f7")),
            new Stop(1, Color.web("#6366f1"))
        );
        iconCircle.setFill(iconGradient);
        
        DropShadow iconGlow = new DropShadow();
        iconGlow.setColor(Color.web("#a855f7"));
        iconGlow.setRadius(20);
        iconGlow.setSpread(0.5);
        iconCircle.setEffect(iconGlow);

        // Try to load calendar icon
        StackPane iconContainer = new StackPane();
        try {
            ImageView calendarIcon = new ImageView(new Image("file:calendar.jpg"));
            calendarIcon.setFitWidth(40);
            calendarIcon.setFitHeight(40);
            calendarIcon.setPreserveRatio(true);
            iconContainer.getChildren().addAll(iconCircle, calendarIcon);
        } catch (Exception ex) {
            // Fallback text icon
            Text calIcon = new Text("📅");
            calIcon.setFont(Font.font(35));
            iconContainer.getChildren().addAll(iconCircle, calIcon);
        }

        // Title
        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.CENTER);
        
        Text welcomeText = new Text("Welcome to ");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        welcomeText.setFill(Color.WHITE);
        
        Text portalText = new Text("Event Portal");
        portalText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        portalText.setFill(Color.web("#a855f7"));
        
        Glow textGlow = new Glow(0.6);
        portalText.setEffect(textGlow);
        
        titleBox.getChildren().addAll(welcomeText, portalText);

        // Subtitle
        Text subtitle = new Text("Manage your events, coordinate with sub-\norganizers, or browse upcoming activities.");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setFill(Color.web("#9ca3af"));
        subtitle.setTextAlignment(TextAlignment.CENTER);

        // Your Events button
        Button eventsButton = new Button("Your Events  →");
        eventsButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        eventsButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #a855f7, #8b5cf6); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 15 40 15 40; " +
            "-fx-cursor: hand;"
        );
        
        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(Color.web("#a855f7"));
        btnGlow.setRadius(15);
        btnGlow.setSpread(0.4);
        eventsButton.setEffect(btnGlow);
        
        applyModernHoverEffect(eventsButton);

        cardBox.getChildren().addAll(iconContainer, titleBox, subtitle, eventsButton);
        
        homeLayout.getChildren().addAll(backContainer, cardBox);
        root.getChildren().add(homeLayout);

        Scene homeScene = new Scene(root);
        Scene eventsScene = createEventsPage(homeScene);
        
        eventsButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.4), eventsScene.getRoot());
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(eventsScene);
            //stage.setMaximized(true);
           stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            fade.play();
        });

        stage.setScene(homeScene);
       // stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

    }

    // ---------------- EVENTS PAGE (ROLE SELECTION) ----------------
    private Scene createEventsPage(Scene homeScene) {
    StackPane root = new StackPane();
    // Bind root to stage size
    root.prefWidthProperty().bind(stage.widthProperty());
    root.prefHeightProperty().bind(stage.heightProperty());

    // Dark gradient background
    LinearGradient bgGradient = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#0f0c29")),
        new Stop(0.5, Color.web("#302b63")),
        new Stop(1, Color.web("#24243e"))
    );
    BackgroundFill bgFill = new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY);
    root.setBackground(new Background(bgFill));

    // Create the Scene early so we can pass it into role cards
    Scene eventsScene = new Scene(root);

    VBox eventRoot = new VBox(40);
    eventRoot.setPadding(new Insets(40));
    eventRoot.setAlignment(Pos.TOP_CENTER);

    // Top bar with back button
    HBox topBar = new HBox(20);
    topBar.setAlignment(Pos.CENTER_LEFT);

    Button backButton = new Button("← Back to Dashboard");
    backButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
    backButton.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.1); " +
        "-fx-text-fill: white; " +
        "-fx-background-radius: 8; " +
        "-fx-padding: 10 20 10 20; " +
        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
        "-fx-border-radius: 8;"
    );
    applyModernHoverEffect(backButton);
    backButton.setOnAction(e -> {
        stage.setScene(homeScene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    });

    topBar.getChildren().add(backButton);

    // Title section
    VBox titleSection = new VBox(10);
    titleSection.setAlignment(Pos.CENTER);

    Text mainTitle = new Text("Select Your Role");
    mainTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
    mainTitle.setFill(Color.WHITE);

    Text subtitle = new Text("Choose a dashboard to proceed");
    subtitle.setFont(Font.font("Segoe UI", 16));
    subtitle.setFill(Color.web("#9ca3af"));

    titleSection.getChildren().addAll(mainTitle, subtitle);

    // Grid layout for role cards
    HBox cardsContainer = new HBox(25);
    cardsContainer.setAlignment(Pos.CENTER);
    cardsContainer.setPadding(new Insets(20));

    String[][] roleData = {
        {"Event Creation", "Main Organizer", "Create new events, manage budgets, and\noversee all operations.", "#10b981", "📅"},
        {"Sub Organizer", "Coordinator", "Manage specific tasks, coordinate teams,\nand handle logistics.", "#f59e0b", "👥"},
        {"All Events", "Viewer", "Browse all upcoming and past events in the\nsystem.", "#3b82f6", "👁"}
    };

    // Pass eventsScene (not homeScene) so child views can return here
    for (String[] roleInfo : roleData) {
        VBox card = createRoleCard(roleInfo[0], roleInfo[1], roleInfo[2], roleInfo[3], roleInfo[4], eventsScene);
        cardsContainer.getChildren().add(card);
    }

    eventRoot.getChildren().addAll(topBar, titleSection, cardsContainer);
    root.getChildren().add(eventRoot);

    return eventsScene;
}

    private VBox createRoleCard(String title, String roleTag, String description, String accentColor, String emoji, Scene eventsScene) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 30, 35, 30));
        card.setPrefSize(330, 380);
        card.setMaxWidth(330);
        card.setStyle(
            "-fx-background-color: rgba(20, 20, 30, 0.6); " +
            "-fx-border-color: rgba(255, 255, 255, 0.08); " +
            "-fx-border-radius: 20; " +
            "-fx-background-radius: 20; " +
            "-fx-border-width: 1;"
        );

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        cardShadow.setRadius(25);
        cardShadow.setSpread(0.1);
        card.setEffect(cardShadow);

        // Icon circle with emoji
        Circle iconCircle = new Circle(50);
        iconCircle.setFill(Color.web(accentColor + "33")); // 20% opacity
        iconCircle.setStroke(Color.web(accentColor));
        iconCircle.setStrokeWidth(2);
        
        DropShadow iconGlow = new DropShadow();
        iconGlow.setColor(Color.web(accentColor));
        iconGlow.setRadius(20);
        iconGlow.setSpread(0.4);
        iconCircle.setEffect(iconGlow);

        Text iconText = new Text(emoji);
        iconText.setFont(Font.font(45));
        
        StackPane iconContainer = new StackPane(iconCircle, iconText);

        // Title
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleText.setFill(Color.WHITE);
        titleText.setTextAlignment(TextAlignment.CENTER);

        // Role tag
        Text roleLabel = new Text(roleTag);
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        roleLabel.setFill(Color.web(accentColor));

        // Description
        Text descText = new Text(description);
        descText.setFont(Font.font("Segoe UI", 14));
        descText.setFill(Color.web("#9ca3af"));
        descText.setTextAlignment(TextAlignment.CENTER);
        descText.setWrappingWidth(270);

        // Button
        String buttonText = title.contains("Event Creation") ? "Open Dashboard" : 
                           title.contains("Sub Organizer") ? "Open Dashboard" : "Open Gallery";
        
        Button actionBtn = new Button(buttonText);
        actionBtn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        actionBtn.setStyle(
            "-fx-background-color: " + accentColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 12 30 12 30; " +
            "-fx-cursor: hand;"
        );
        
        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(Color.web(accentColor + "80"));
        btnGlow.setRadius(12);
        btnGlow.setSpread(0.3);
        actionBtn.setEffect(btnGlow);

        // Button hover effect
        actionBtn.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), actionBtn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            
            DropShadow hoverGlow = new DropShadow();
            hoverGlow.setColor(Color.web(accentColor));
            hoverGlow.setRadius(15);
            hoverGlow.setSpread(0.5);
            actionBtn.setEffect(hoverGlow);
            actionBtn.setCursor(javafx.scene.Cursor.HAND);
        });

        actionBtn.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), actionBtn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            actionBtn.setEffect(btnGlow);
        });

        // Determine role type from title
        String role = title.contains("Event Creation") ? "Main Organizer" :
                     title.contains("Sub Organizer") ? "Sub Organizer" : "Viewer";

        actionBtn.setOnAction(e -> {
            try {
                switch (role) {
                    case "Main Organizer" -> {
                        String mainName = Session.getUserName();
                        int mainId = Session.getUserId();
                        new MainOrganizerView(stage, eventsScene, mainName, mainId);
                        stage.setFullScreen(true); // fullscreen
                stage.setFullScreenExitHint("");
                    }
                    case "Sub Organizer" -> {
                        String subName = Session.getUserName();
                        new SubOrganizerView(stage, eventsScene, subName);
                        stage.setFullScreen(true); // fullscreen
                stage.setFullScreenExitHint("");
                    }
                    default -> {new ViewerView(stage, eventsScene);
stage.setFullScreen(true); // fullscreen
                stage.setFullScreenExitHint("");

                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                    "View open korte gele error holo:\n" + ex.getMessage())
                    .showAndWait();
            }
        });

        // Card hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: rgba(30, 30, 45, 0.7); " +
                "-fx-border-color: " + accentColor + "80; " +
                "-fx-border-radius: 20; " +
                "-fx-background-radius: 20; " +
                "-fx-border-width: 1.5;"
            );
            
            DropShadow hoverShadow = new DropShadow();
            hoverShadow.setColor(Color.web(accentColor + "60"));
            hoverShadow.setRadius(30);
            hoverShadow.setSpread(0.3);
            card.setEffect(hoverShadow);
            
            card.setCursor(javafx.scene.Cursor.HAND);
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: rgba(20, 20, 30, 0.6); " +
                "-fx-border-color: rgba(255, 255, 255, 0.08); " +
                "-fx-border-radius: 20; " +
                "-fx-background-radius: 20; " +
                "-fx-border-width: 1;"
            );
            card.setEffect(cardShadow);
        });

        card.getChildren().addAll(iconContainer, titleText, roleLabel, descText, actionBtn);
        return card;
    }
}