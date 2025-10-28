import java.util.ArrayList;
import java.util.List;

public class EventStore {
    private static final List<Event> submittedEvents = new ArrayList<>();

    public static void addEvent(Event event) {
        submittedEvents.add(event);
    }

    public static List<Event> getSubmittedEvents() {
        return submittedEvents;
    }
}
