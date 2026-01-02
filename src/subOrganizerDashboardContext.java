public class subOrganizerDashboardContext implements OrganizerViewContext {
    @Override
    public boolean shouldShowButton(String buttonName) {
        // From dashboard - show limited features
        return switch (buttonName) {
            case "tasks" -> true;
            case "chat" -> true;
            default -> true;
        };
    }
    
    @Override
    public String getContextName() {
        return "Dashboard";
    }
}