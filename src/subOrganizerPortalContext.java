public class subOrganizerPortalContext implements OrganizerViewContext {
    @Override
    public boolean shouldShowButton(String buttonName) {
        
        return switch (buttonName) {      
            case "chat" -> false;           
            default -> true;             
        };
    }
    
    @Override
    public String getContextName() {
        return "Event Portal";
    }
}
