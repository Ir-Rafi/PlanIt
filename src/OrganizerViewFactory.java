
// ============================================================
// 5. ORGANIZER VIEW FACTORY
// ============================================================

public class OrganizerViewFactory {

    /**
     * Create and open appropriate organizer view
     * @param stage Current stage
     * @param eventScene Event scene for back button
     * @param organizerName Name of the organizer
     * @param organizerId ID of the organizer (0 for sub organizer)
     * @param isMainOrganizer True if main organizer, false if sub organizer
     */
    public static OrganizerPanel openOrganizerView(
            javafx.stage.Stage stage,
            javafx.scene.Scene eventScene,
            String organizerName,
            int organizerId,
            boolean isMainOrganizer) {

        if (isMainOrganizer) {
            return new MainOrganizerView(stage, eventScene, organizerName, organizerId);
        } else {
            return new SubOrganizerView(stage, eventScene, organizerName);
        }
    }
}

