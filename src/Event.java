import java.time.LocalDate;
import java.util.List;

public class Event {
    private String name;
    private LocalDate date;
    private String mainOrganizerName;
    private String mainOrganizerPhone;
    private List<Organizer> organizers;

    public Event(String name, LocalDate date, String mainOrganizerName, String mainOrganizerPhone, List<Organizer> organizers) {
        this.name = name;
        this.date = date;
        this.mainOrganizerName = mainOrganizerName;
        this.mainOrganizerPhone = mainOrganizerPhone;
        this.organizers = organizers;
    }

    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getMainOrganizerName() { return mainOrganizerName; }
    public String getMainOrganizerPhone() { return mainOrganizerPhone; }
    public List<Organizer> getOrganizers() { return organizers; }
}
