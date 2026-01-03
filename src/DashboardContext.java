public class DashboardContext implements OrganizerViewContext {

    @Override
    public boolean shouldShowButton(String buttonName) {
        return switch (buttonName) {
            case "bookPlaces", "createEvent", "progressReport", "todo" -> false;
            default -> true;
        };
    }

    @Override
    public String getContextName() {
        return "Dashboard";
    }    
}
