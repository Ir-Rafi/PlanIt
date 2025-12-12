

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx. scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx. stage.Stage;

public class MainOrganizerView extends after_login implements OrganizerPanel {
    // Add these fields at the top
    protected Stage stage;
    protected Scene eventListScene;
    private final String mainOrganizerName;
    private final int mainOrganizerId;
    private final mainOrganizerViewContext context;

    public MainOrganizerView(Stage stage, Scene eventListScene, String mainOrganizerName, int mainOrganizerId) {
        this(stage, eventListScene, mainOrganizerName, mainOrganizerId, new EventPortalContext());
    }

    public MainOrganizerView(Stage stage, Scene eventListScene, String mainOrganizerName, int mainOrganizerId, mainOrganizerViewContext context) {
        this.stage = stage;
        this.eventListScene = eventListScene;
        this.mainOrganizerName = mainOrganizerName;
        this.mainOrganizerId = mainOrganizerId;
        this.context = context;

        System.out.println("🎯 MainOrganizerView created with context: " + context.getContextName());

        // Keep ALL existing UI code here - NO CHANGES
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        iconContainer.setStyle("-fx-background-color: #1e3a5f; -fx-background-radius: 10;");

        SVGPath gearIcon = new SVGPath();
        gearIcon.setContent("M12 15.5A3.5 3.5 0 0 1 8.5 12 3.5 3.5 0 0 1 12 8.5a3.5 3.5 3.5 0 0 1 3.5 3.5m7.43-2.53c.04-.32.07-.64.07-.97 0-.33-.03-.66-.07-1l2.11-1.63c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.31-.61-.22l-2.49 1c-.52-.39-1.06-.73-1.69-.98l-.37-2.65A.506.506 0 0 0 14 2h-4c-.25 0-.46.18-.5.42l-.37 2.65c-.63.25-1.17.59-1.69.98l-2.49-1c-.22-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64L4.57 11c-.04.34-.07.67-.07 1 0 .33.03.65.07.97l-2.11 1.66c-.19.15-.25.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1.01c.52.4 1.06.74 1.69.99l.37 2.65c.04.24.25.42.5.42h4c.25 0 .46-.18.5-.42l.37-2.65c.63-.26 1.17-.59 1.69-.99l2.49 1.01c.22.08.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.66z");
        gearIcon.setFill(Color.web("#6366f1"));
        gearIcon.setScaleX(1.2);
        gearIcon.setScaleY(1.2);
        iconContainer.getChildren().add(gearIcon);

        Label title = new Label("Organizer Dashboard");
        title.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage your events and team efficiently");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(450);
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 20; " +
                "-fx-border-color: #334155; -fx-border-radius: 20; -fx-border-width: 1;");

        Button assignRolesBtn = createStyledButton("Assign Roles to Members", "#7c3aed", "#8b5cf6", "👥");
        Button todoBtn = createStyledButton("Create To-Do List", "#3b82f6", "#60a5fa", "📋");
        Button createNewEventBtn = createStyledButton("Create New Event", "#22c55e", "#4ade80", "➕");
        Button bookPlacesBtn = createStyledButton("Book Event Places", "#06b6d4", "#22d3ee", "📍");
        Button progressReportBtn = createStyledButton("Manage Progress Reports", "#10b981", "#34d399", "📊");
        Button chatBtn = createStyledButton("Open Chat", "#8b5cf6", "#a78bfa", "💬");

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxWidth(400);
        separator.setStyle("-fx-background-color: #334155;");

        Button backBtn = new Button("← Back to Home");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; " +
                "-fx-font-size: 14px; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-cursor: hand;"));

        assignRolesBtn.setOnAction(e -> new AssignRolesWindow(mainOrganizerName, mainOrganizerId).show());
        todoBtn.setOnAction(e -> {
            Scene organizerScene = stage.getScene();
            new AdvancedTodoListApp(organizerScene).start(stage);
        });
        bookPlacesBtn.setOnAction(e -> {
            int eventId = 0;
            new BookingPage(stage, stage.getScene(), eventId);
        });
        progressReportBtn.setOnAction(e -> new ProgressReportWindow().show());
        chatBtn.setOnAction(e -> chatWindows.openServerChat(mainOrganizerName));

        backBtn.setOnAction(e -> {
            stage.setScene(eventListScene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        });

        createNewEventBtn.setOnAction(e -> {
            try {
                EventController.openEventForm(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        if (context.shouldShowButton("assignRoles")) card.getChildren().add(assignRolesBtn);
        if (context.shouldShowButton("todo")) card.getChildren().add(todoBtn);
        if (context.shouldShowButton("bookPlaces")) card.getChildren().add(bookPlacesBtn);
        if (context.shouldShowButton("progressReport")) card.getChildren().add(progressReportBtn);
        if (context.shouldShowButton("createEvent")) card.getChildren().add(createNewEventBtn);
        if (context.shouldShowButton("chat")) card.getChildren().add(chatBtn);

        card.getChildren().addAll(
                separator,
                backBtn
        );

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(iconContainer, title, subtitle);

        layout.getChildren().addAll(topSpacer, header, card, bottomSpacer);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    private Button createStyledButton(String text, String baseColor, String hoverColor, String icon) {
        Button button = new Button(icon + "   " + text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(50);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 20, 0, 20));

        String baseStyle = "-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: 600; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 4);";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: 600; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 6);";

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    // ============================================================
    // INTERFACE IMPLEMENTATIONS
    // ============================================================
    @Override
    public void initialize() {
        // Initialization logic if needed
    }

    @Override
    public String getOrganizerType() {
        return "Main Organizer";
    }

    @Override
    public int getOrganizerId() {
        return mainOrganizerId;
    }

    @Override
    public String getOrganizerName() {
        return mainOrganizerName;
    }
}