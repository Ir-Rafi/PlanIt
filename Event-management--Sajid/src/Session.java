public class Session {
    private static int userId;
    private static String userName;
    private static String role;

    // ✅ Unified setter
    public static void setUser(int id, String name, String role) {
        userId = id;
        userName = name;
        Session.role = role;
    }

    // ✅ For older code using setSession(id, name)
    public static void setSession(int id, String name) {
        userId = id;
        userName = name;
        Session.role = "Main Organizer"; // or default
    }

    // ✅ Accessors
    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getRole() {
        return role;
    }

    // ✅ Compatibility aliases for older code
    public static int getOrganizerId() {
        return userId;
    }

    public static String getUsername() {
        return userName;
    }
}
