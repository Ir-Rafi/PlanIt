import javafx.stage.Stage;
import javafx.scene.Scene;

public abstract class BaseOrganizerView implements OrganizerPanel {
    protected Stage stage;
    protected Scene eventsScene;
    protected String organizerName;
    protected int organizerId;

    public BaseOrganizerView(Stage stage, Scene eventsScene, String organizerName, int organizerId) {
        this.stage = stage;
        this.eventsScene = eventsScene;
        this.organizerName = organizerName;
        this.organizerId = organizerId;
    }

    @Override
    public int getOrganizerId() {
        return organizerId;
    }

    @Override
    public String getOrganizerName() {
        return organizerName;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getEventsScene() {
        return eventsScene;
    }

    @Override
    public abstract void initialize();

    @Override
    public abstract String getOrganizerType();
}